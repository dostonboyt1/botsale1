import React, {Component} from 'react';
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import {connect} from "react-redux";
import Pagination from "react-js-pagination";


@connect(({app, brand1}) => ({app, brand1}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showBrandSaveOrEditModal: false,
      currentBrand: '',
      tempBrandId: '',
      showDeletingModal: false,
      activePage: 1
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'brand1/getBrandByPageable',
      payload: {
        page: 0,
        size: 10
      }
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
      type: 'brand1/getBrandByPageable',
      payload: {
        page: pageNumber - 1,
        size: 10
      }
    })

  }

  render() {
    const {dispatch, app, brand1} = this.props
    console.log(this.props, "PROPS====================")
    const {brandPage, brandTotalElements} = brand1
    const {getAllBrands} = app
    const toggleBrandSaveOrEditModal = () => {
      this.setState({
        showBrandSaveOrEditModal: !this.state.showBrandSaveOrEditModal,
        currentBrand: ''
      })
    }
    const changeEnabled = (id, active) => {
      console.log(id, 'asdfasdf')
      dispatch({
        type: 'brand1/changeActiveBrand',
        payload: {
          id,
          active
        }
      }).then(res => {
        dispatch({
          type: 'brand1/getBrandByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
      })
    }

    const editBrand = (item) => {
      this.setState({
        showBrandSaveOrEditModal: !this.state.showBrandSaveOrEditModal,
        currentBrand: item
      })
    }
    const changeActive = (item) => {
      this.state({})
    }
    const brandSaveOrEdit = (e, v) => {
      console.log(v, "VVVVVVVV")
      if (this.state.currentBrand) {
        v = {...v, id: this.state.currentBrand.id}
      }
      dispatch({
        type: 'brand1/brandSaveOrEdit',
        payload: v
      }).then(res => {
        dispatch({
          type: 'brand1/getBrandByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })

        this.setState({
          showBrandSaveOrEditModal: !this.state.showBrandSaveOrEditModal,
          currentBrand: ''
        })
      })
    }


    const deleteBrand = (brandId) => {
      this.setState({tempBrandId: brandId, showDeletingModal: !this.state.showDeletingModal})

    }

    const toggleBrandDeleteModal = () => {
      this.setState({tempBrandId: '', showDeletingModal: !this.state.showDeletingModal})
    }

    const konkretOchirish = () => {
      if (this.state.tempBrandId) {
        dispatch({
          type: 'brand1/brandDelete',
          payload: {
            id: this.state.tempBrandId
          }
        }).then(res => {
          dispatch({
            type: 'brand1/getBrandByPageable',
            payload: {
              page: 0,
              size: 10
            }
          })
          dispatch({
            type: 'app/getAllBrands'
          })
          this.setState({showDeletingModal: !this.state.showDeletingModal})
        })
      }
    }
    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h3>Brendlar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleBrandSaveOrEditModal}>+</Button>
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
                <th>Active</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {brandPage ?
                brandPage.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.nameUz}</td>
                    <td>{item.nameRu}</td>
                    <td><input type="checkbox" checked={item.active}/></td>
                    <td>
                      <Button color="warning" onClick={() => editBrand(item)}>Edit</Button>

                      <Button className="ml-3" color="info"
                              onClick={() => changeEnabled(item.id, item.active ? false : true)}>{item.active ? "Bloklash" : "Aktivlashtirish"}</Button>

                      <Button className="ml-3" onClick={() => deleteBrand(item.id)} color="danger">Delete</Button>
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
              totalItemsCount={brandTotalElements}
              pageRangeDisplayed={5}
              onChange={this.handlePageChange.bind(this)} itemClass="page-item"
              linkClass="page-link"
            />
          </Col>
        </Row>

        <Modal isOpen={this.state.showBrandSaveOrEditModal} toggle={toggleBrandSaveOrEditModal}>
          <ModalHeader
            toggle={this.modalControl}>{this.state.currentBrand ? "Brendni o'zgartirish" : "Yangi brend qo'shish"}</ModalHeader>
          <AvForm onValidSubmit={brandSaveOrEdit}>
            <ModalBody>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentBrand ? this.state.currentBrand.nameUz : ''} className="mt-2"
                       placeholder="NameUz" name="nameUz"/>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentBrand ? this.state.currentBrand.nameRu : ''} className="mt-2"
                       placeholder="NameRu" name="nameRu"/>
            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={toggleBrandSaveOrEditModal}>Bekor qilish</Button>

              <Button className="ml-3" color="success" type="submit">Saqlash</Button>
            </ModalFooter>
          </AvForm>
        </Modal>

        <Modal isOpen={this.state.showDeletingModal} toggle={toggleBrandDeleteModal}>
          <ModalHeader toggle={this.modalControl}>Brandni o'chirishni istaysizmi?</ModalHeader>

          <ModalBody>

          </ModalBody>
          <ModalFooter>
            <Button color="info" onClick={toggleBrandDeleteModal}>Bekor qilish</Button>
            <Button className="ml-3" color="danger" onClick={konkretOchirish} type="button">O'chirish</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
