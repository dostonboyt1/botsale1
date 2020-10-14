import React, {Component} from 'react';
import {connect} from "react-redux";
import {Button, Col, Label, Input, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import CurrencyInput from "react-currency-input";
import Pagination from "react-js-pagination";

@connect(({product, app}) => ({product, app}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showProductSaveOrEditModal: false,
      currentProduct: '',
      tempPhotoId: '',
      activePage: 1
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'product/getAllProductsByPageable',
      payload: {
        page: 0,
        size: 10
      }
    })
    dispatch({
      type: 'app/getAllCategories'
    })
    dispatch({
      type: 'app/getAllProductSize'
    })
    dispatch({
      type: 'app/getAllBrands'
    })
  }

  handlePageChange(pageNumber) {
    const {dispatch} = this.props
    console.log(`active page is ${pageNumber}`);
    this.setState({activePage: pageNumber});
    dispatch({
      type: 'product/getAllProductsByPageable',
      payload: {
        page: pageNumber - 1,
        size: 10
      }
    })

  }

  render() {
    const {dispatch, product, app} = this.props
    const {productPage, productTotalElements} = product
    const {allCategories, allProductSize, allBrands} = app

    const toggleProductSaveOrEditModal = () => {
      this.setState({
        showProductSaveOrEditModal: !this.state.showProductSaveOrEditModal,
        currentProduct: ''
      })
    }
    const editProduct = (item) => {
      this.setState({
        showProductSaveOrEditModal: !this.state.showProductSaveOrEditModal,
        currentProduct: item,
        tempPhotoId: item.photoId
      })
    }
    const deleteProduct = (id) => {
      dispatch({
        type: 'product/deleteProduct',
        payload: {
          id
        }
      }).then(res => {
        dispatch({
          type: 'product/getAllProductsByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
      })
    }
    const productSaveOrEdit = (e, v) => {
      console.log(v, "VALUES Before")
      if (this.state.currentProduct) {
        v = {...v, id: this.state.currentProduct.id}
      }
      if (this.state.currentProduct) {
        if (this.state.currentProduct.incomePrice == v.incomePrice) {

        } else {
          v.incomePrice = v.incomePrice.replace(/ /g, '')
        }
        if (this.state.currentProduct.salePrice == v.salePrice) {

        } else {
          v.salePrice = v.salePrice.replace(/ /g, '')
        }
      } else {
        v.salePrice = v.salePrice.replace(/ /g, '')
        v.incomePrice = v.incomePrice.replace(/ /g, '')
      }
      console.log(v, "VALUES After")
      if (this.state.tempPhotoId) {
        v = {...v, photoId: this.state.tempPhotoId}
      }
      dispatch({
        type: 'product/productSaveOrEdit',
        payload: v
      }).then(res => {
        dispatch({
          type: 'product/getAllProductsByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
        toggleProductSaveOrEditModal();
      })
    }
    const getPhotoId = (e) => {
      dispatch({
        type: 'app/addOrEditFile',
        payload: {
          file: e.target.files[0],
          fileUpload: true,
          type: e.target.name
        }
      }).then(res => {
        this.setState({tempPhotoId: res.object[0].fileId})
      })
    }
    const productSearch=(e)=>{
      console.log(e.target.value,"value search")
      if (e.target.value){
        dispatch({
          type: 'product/getAllProductsByPageable',
          payload: {
            page: 0,
            size: 10,
            search:e.target.value
          }
        })
      }else {
        dispatch({
          type: 'product/getAllProductsByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
      }
    }
    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h3>Maxsulotlar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleProductSaveOrEditModal}>+</Button>
          </Col>
          <Col>
            <Input type="text" placeholder="search" onChange={productSearch}/>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Photo</th>
                <th>NameUz</th>
                <th>NameRu</th>
                <th>DescriptionUz</th>
                <th>DescriptionRu</th>
                <th>Norm</th>
                <th>Kelish narxi</th>
                <th>Sotish narxi</th>
                <th>Razmerlari</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {productPage ? productPage.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>
                      <div style={{width: "40px", height: "40px"}}><img
                        src={item.photoId ? "http://localhost:/api/file/" + item.photoId : ''} alt=""
                        className="img-fluid"/></div>
                    </td>
                    <td>{item.nameUz}</td>
                    <td>{item.nameRu}</td>
                    <td>{item.descriptionUz}</td>
                    <td>{item.descriptionRu}</td>
                    <td>{item.norm.toFixed().toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1 ")}</td>
                    <td>{item.incomePrice.toFixed().toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1 ")}</td>
                    <td>{item.salePrice.toFixed().toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1 ")}</td>
                    <td>{item.productSizeList ? item.productSizeList.map(itemm =>
                      <span>{itemm.name}<br/></span>) : ''}</td>
                    <td>
                      <Button color="warning" onClick={() => editProduct(item)}>Edit</Button>

                      <Button className="ml-3" color="danger" onClick={() => deleteProduct(item.id)}>Delete</Button>
                    </td>
                  </tr>
                )
                : ''}
              </tbody>
            </Table>
          </Col>
        </Row>
        <Row>
          <Pagination
            activePage={this.state.activePage}
            itemsCountPerPage={10}
            totalItemsCount={productTotalElements}
            pageRangeDisplayed={5}
            onChange={this.handlePageChange.bind(this)} itemClass="page-item"
            linkClass="page-link"
          />
        </Row>

        <Modal isOpen={this.state.showProductSaveOrEditModal} toggle={toggleProductSaveOrEditModal}>
          <ModalHeader
            toggle={this.modalControl}>{this.state.currentCategory ? "Kategoriyani o'zgartirish" : "Yangi kategoriya qo'shish"}</ModalHeader>
          <AvForm onValidSubmit={productSaveOrEdit}>
            <ModalBody>
              <div>
                <input type="file" className="form-control" placeholder="Rasm tanlang" onChange={getPhotoId}/>
                <div style={{width: "200px", height: "200px"}}><img
                  src={this.state.tempPhotoId ? "http://localhost:/api/file/" + this.state.tempPhotoId : ''} alt=""
                  className="img-fluid"/></div>
              </div>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentProduct ? this.state.currentProduct.nameUz : ''} className="mt-2"
                       placeholder="NameUz" name="nameUz"/>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentProduct ? this.state.currentProduct.nameRu : ''} className="mt-2"
                       placeholder="NameUz" name="nameRu"/>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentProduct ? this.state.currentProduct.descriptionUz : ''}
                       className="mt-2" placeholder="DescriptionUz" name="descriptionUz"/>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentProduct ? this.state.currentProduct.descriptionRu : ''}
                       className="mt-2" placeholder="DescriptionRu" name="descriptionRu"/>
              <Label>Kelish narxini kiriting :</Label>
              <AvField tag={CurrencyInput}
                       className='border-grey currency-input form-control mb-3 border-0 bg-light'
                       thousandSeparator=" "
                       precision="0"
                       value={this.state.currentProduct ? this.state.currentProduct.incomePrice : ''}
                       required={true}
                       name="incomePrice"
                       placeholder="Kelish narxi"
                       type='text'/>
              <Label>Sotish narxini kiriting :</Label>
              <AvField tag={CurrencyInput}
                       className='border-grey currency-input form-control mb-3 border-0 bg-light'
                       thousandSeparator=" "
                       precision="0"
                       value={this.state.currentProduct ? this.state.currentProduct.salePrice : ''}
                       required={true}
                       name="salePrice"
                       placeholder="Sotish narxi"
                       type='text'/>
              <Label>Normani kiriting :</Label>
              <AvField tag={CurrencyInput}
                       className='border-grey currency-input form-control mb-3 border-0 bg-light'
                       thousandSeparator=" "
                       precision="0"
                       value={this.state.currentProduct ? this.state.currentProduct.norm : ''}
                       required={true}
                       name="norm"
                       placeholder="Dona soni"
                       type='text'/>
              <Label>Razmerlarni tanlang:</Label>
              <AvField type="select" multiple className="mt-2" placeholder="Razmerlarni tanlang" name="productSizeList">
                <option>Razmerlarni tanlang</option>
                {allProductSize ? allProductSize.map(item =>
                  <option value={item.id}>{item.name}</option>
                ) : ''}
              </AvField>
              <Label>Kategoriya tanlang:</Label>
              <AvField type="select"
                       defaultValue={this.state.currentProduct.resCategory ? this.state.currentProduct.resCategory.id : ''}
                       className="mt-2" placeholder="Kategoriyasini tanlang" name="categoryId">
                <option>Kategoriyani tanlang</option>
                {allCategories ? allCategories.map(item =>
                  <option value={item.id}>{item.nameUz}</option>
                ) : ''}
              </AvField>
              <Label>Brand tanlang:</Label>
              <AvField type="select"
                       defaultValue={this.state.currentProduct.brand ? this.state.currentProduct.brand.id : ''}
                       className="mt-2" placeholder="Ota categoriyasini tanlang" name="brandId">
                <option>Brandni tanlang</option>
                {allBrands ? allBrands.map(item =>
                  <option value={item.id}>{item.nameUz}</option>
                ) : ''}
              </AvField>
              <Label>Gender tanlang:</Label>
              <AvField type="select" defaultValue={this.state.currentProduct ? this.state.currentProduct.gender : ''}
                       className="mt-2" placeholder="Gender tanlang" name="gender">
                <option>GenderTanlang</option>
                <option value="MALE">Erkaklar uchun</option>
                <option value="FEMALE">Ayollar uchun</option>
                <option value="KIDS">Bolalar uchun</option>
              </AvField>
            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={toggleProductSaveOrEditModal}>Bekor qilish</Button>

              <Button className="ml-3" color="success" type="submit">Saqlash</Button>
            </ModalFooter>
          </AvForm>
        </Modal>

        {/*<Modal isOpen={this.state.showDeletingModal} toggle={toggleCategoryDeleteModal}>*/}
        {/*  <ModalHeader toggle={this.modalControl}>Kategoriyani o'chirishni istaysizmi?</ModalHeader>*/}

        {/*  <ModalBody>*/}

        {/*  </ModalBody>*/}
        {/*  <ModalFooter>*/}
        {/*    <Button color="danger" onClick={toggleCategoryDeleteModal}>Bekor qilish</Button>*/}
        {/*    <Button className="ml-3" color="info" type="button" onClick={konkretOchirish}>O'chirish</Button>*/}
        {/*  </ModalFooter>*/}
        {/*</Modal>*/}
      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
