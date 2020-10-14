package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pdp.botsale.entity.Attachment;
import uz.pdp.botsale.entity.AttachmentContent;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.repository.AttachmentContentRepository;
import uz.pdp.botsale.repository.AttachmentRepository;
import uz.pdp.botsale.security.CurrentUser;
import uz.pdp.botsale.service.AttachmentService;
import uz.pdp.botsale.service.FileServlet;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class AttachmentController {

    @Autowired
    AttachmentService attachmentService;
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    @PostMapping
    public ApiResponseModel uploadFile(MultipartHttpServletRequest request, @CurrentUser User user) throws IOException {
        return attachmentService.uploadFile(request,user);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getFile(@PathVariable UUID id, HttpServletResponse response) {
        return attachmentService.getAttachmentContent(id, response);
    }
    @GetMapping("/content/{id}")
    public HttpEntity<?> getFileContent(@PathVariable UUID id, HttpServletResponse response) {

        return ResponseEntity.ok(new ApiResponseModel(true,"attachmentContent",attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("attachment not found", "id", id))));
    }
    @PermitAll
    @GetMapping("/deleteByNameAndSize/{name}/{size}")
    public HttpEntity<?> deleteByNameAndSize(@PathVariable(value = "name")String name, @PathVariable(value = "size") long size, @CurrentUser User user){
        List<Attachment> attachments=attachmentRepository.findAllByNameAndSize(name,size);
        if (attachments.size()>0){
        attachments.forEach(attachment -> {
           Optional<AttachmentContent> attachmentContentOptional= attachmentContentRepository.findByAttachment(attachment);
           if (attachmentContentOptional.isPresent()){
               attachmentContentRepository.delete(attachmentContentOptional.get());
           }
            attachmentRepository.delete(attachment);

        });
        return ResponseEntity.ok(new ApiResponse("file deleted",true));}
        return ResponseEntity.ok(new ApiResponse("file not found",false));
    }


    @DeleteMapping("/{id}")
    public HttpEntity<?> delAttachment(@PathVariable UUID id){
        Optional<Attachment> byId = attachmentRepository.findById(id);
        if (byId.isPresent()){
            Optional<AttachmentContent> byAttachment = attachmentContentRepository.findByAttachment(byId.get());
        if (byAttachment.isPresent()){
            attachmentContentRepository.delete(byAttachment.get());
            attachmentRepository.delete(byId.get());
            return ResponseEntity.ok(new ApiResponse("deleted",true));

        }
        }
        return ResponseEntity.ok(new ApiResponse("Error",false));
    }


    @GetMapping("/byByte/{id}")
    public void getFileByByte(@PathVariable UUID id, HttpServletRequest request, HttpServletResponse response){
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()){
           Attachment attachment= optionalAttachment.get();
            AttachmentContent file = attachmentContentRepository.getByAttachment(attachment);
//            response.setContentType(file.getAttachment().getContentType());
            Path path;
            try {
                path = Files.write(Paths.get(file.getAttachment().getName()), file.getContent());
                File video = path.toFile();
                FileServlet fileServlet = new FileServlet(video);
                fileServlet.processRequest(request, response, true, file.getAttachment().getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
