import React, {Component} from 'react';
import {connect} from "react-redux";
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import Pagination from "react-js-pagination";



@connect(({app, cat}) => ({app, cat}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showCategorySaveOrEditModal: false,
      currentCategory:'',
      tempCategoryId:'',
      showDeletingModal:false,
      activePage:1
    }
  }
  handlePageChange(pageNumber) {
    this.setState({activePage: pageNumber});
    const {dispatch} = this.props;
    dispatch({
      type: 'cat/getAllCategoriesByPageable',
      payload: {
        page: pageNumber-1,
        size: 10
      }
    })
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'cat/getAllCategoriesByPageable',
      payload: {
        page: 0,
        size: 10
      }
    })
    dispatch({
      type: 'app/getAllCategories'
    })
  }

  render() {
    const {dispatch, app, cat} = this.props
    console.log(this.props, "PROPS====================")
    const {catPage, catTotalElements} = cat
    const {allCategories}=app
    const toggleCategorySaveOrEditModal = () => {
      this.setState({showCategorySaveOrEditModal:!this.state.showCategorySaveOrEditModal,currentCategory:''})
    }
    const editCategory=(item)=>{
      this.setState({showCategorySaveOrEditModal:!this.state.showCategorySaveOrEditModal,currentCategory:item})
    }
    const categorySaveOrEdit=(e,v)=>{
      console.log(v,"VVVVVVVV")
      if (this.state.currentCategory){
        v={...v,id:this.state.currentCategory.id}
      }
      dispatch({
        type:'cat/categorySaveOrEdit',
        payload:v
      }).then(res=>{
        dispatch({
          type: 'cat/getAllCategoriesByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
        dispatch({
          type: 'app/getAllCategories'
        })
        this.setState({showCategorySaveOrEditModal:!this.state.showCategorySaveOrEditModal,currentCategory:''})
      })
    }
    const deleteCategory=(categoryId)=>{
      this.setState({tempCategoryId:categoryId,showDeletingModal:!this.state.showDeletingModal})

    }
    const toggleCategoryDeleteModal=()=>{
      this.setState({tempCategoryId:'',showDeletingModal:!this.state.showDeletingModal})
    }
    const konkretOchirish=()=>{
      if (this.state.tempCategoryId){
        dispatch({
          type:'cat/deleteCategory',
          payload:{
            id:this.state.tempCategoryId
          }
        }).then(res=>{
          dispatch({
            type: 'cat/getAllCategoriesByPageable',
            payload: {
              page: 0,
              size: 10
            }
          })
          dispatch({
            type: 'app/getAllCategories'
          })
          this.setState({showDeletingModal:!this.state.showDeletingModal})
        })
      }
    }
    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h3>Kategoriyalar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleCategorySaveOrEditModal}>+</Button>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>NameUz</th>
                <th>NameRu</th>
                <th>ParentNameUz</th>
                <th>ParentNameRu</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {catPage ?
                catPage.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.nameUz}</td>
                    <td>{item.nameRu}</td>
                    <td>{item.parentNameUz}</td>
                    <td>{item.parentNameRu}</td>
                    <td>
                      <Button color="warning" onClick={() => editCategory(item)}>Edit</Button>

                      <Button className="ml-3" color="danger" onClick={()=>deleteCategory(item.id)}>Delete</Button>
                    </td>
                  </tr>
                )
                : ''}
              </tbody>
            </Table>
          </Col>
        </Row>
        <Row>
          <Col>
            <Pagination
              activePage={this.state.activePage}
              itemsCountPerPage={10}
              totalItemsCount={catTotalElements}
              pageRangeDisplayed={5}
              onChange={this.handlePageChange.bind(this)}
              itemClass="page-item"
              linkClass="page-link"
            />
          </Col>
        </Row>

        <Modal isOpen={this.state.showCategorySaveOrEditModal} toggle={toggleCategorySaveOrEditModal}>
          <ModalHeader toggle={this.modalControl}>{this.state.currentCategory?"Kategoriyani o'zgartirish":"Yangi kategoriya qo'shish"}</ModalHeader>
         <AvForm onValidSubmit={categorySaveOrEdit}>
          <ModalBody>
            <AvField required={true} type="text" defaultValue={this.state.currentCategory?this.state.currentCategory.nameUz:''} className="mt-2" placeholder="NameUz" name="nameUz"/>
            <AvField required={true} type="text" defaultValue={this.state.currentCategory?this.state.currentCategory.nameRu:''} className="mt-2" placeholder="NameRu" name="nameRu"/>
            <AvField type="select" defaultValue={this.state.currentCategory?this.state.currentCategory.parentId:''} className="mt-2" placeholder="Ota categoriyasini tanlang" name="parentId">
              <option>Ota kategoriyani tanlang</option>
              {allCategories?allCategories.map(item=>
                <option value={item.id}>{item.nameUz}</option>
              ):''}
            </AvField>
          </ModalBody>
          <ModalFooter>
            <Button color="danger" onClick={toggleCategorySaveOrEditModal}>Bekor qilish</Button>

            <Button className="ml-3" color="success" type="submit">Saqlash</Button>
          </ModalFooter>
         </AvForm>
        </Modal>

        <Modal isOpen={this.state.showDeletingModal} toggle={toggleCategoryDeleteModal}>
          <ModalHeader toggle={this.modalControl}>Kategoriyani o'chirishni istaysizmi?</ModalHeader>

            <ModalBody>

            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={toggleCategoryDeleteModal}>Bekor qilish</Button>
              <Button className="ml-3" color="info" type="button" onClick={konkretOchirish}>O'chirish</Button>
            </ModalFooter>
        </Modal>
      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
