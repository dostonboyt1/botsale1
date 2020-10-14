import React, {Component} from 'react';
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import {connect} from "react-redux";
import Pagination from "react-js-pagination";


@connect(({app, productSize}) => ({app, productSize}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showProductSizeSaveOrEditModal: false,
      currentProductSize: '',
      tempProductSizeId: '',
      showDeletingModal: false,
      activePage: 1
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'productSize/getProductSizeByPageable',
      payload: {
        page: 0,
        size: 10
      }
    })
    // dispatch({
    //   type: 'app/getAllProductSize'
    // })
  }
  handlePageChange(pageNumber) {
    const {dispatch} = this.props
    console.log(`active page is ${pageNumber}`);
    this.setState({activePage: pageNumber});
    dispatch({
      type: 'productSize/getProductSizeByPageable',
      payload: {
        page: pageNumber-1,
        size: 10
      }
    })

  }

  render() {
    const {dispatch, app, productSize} = this.props
    console.log(this.props, "PROPS====================")
    const {productSizePage, productSizeTotalElements} = productSize
    console.log(productSizePage,"Productsize page+++++++++++")
    const {getAllProductSize} = app
    const toggleProductSizeSaveOrEditModal = () => {
      this.setState({
        showProductSizeSaveOrEditModal: !this.state.showProductSizeSaveOrEditModal,
        currentProductSize: ''
      })
    }
    const editProductSize = (item) => {
      this.setState({
        showProductSizeSaveOrEditModal: !this.state.showProductSizeSaveOrEditModal,
        currentProductSize: item
      })
    }
    const productSizeSaveOrEdit = (e, v) => {
      console.log(v, "VVVVVVVV")
      if (this.state.currentProductSize) {
        v = {...v, id: this.state.currentProductSize.id}
      }
      dispatch({
        type: 'productSize/saveOrEditProductSize',
        payload: v
      }).then(res => {
        dispatch({
          type: 'productSize/getProductSizeByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })

        this.setState({
          showProductSizeSaveOrEditModal: !this.state.showProductSizeSaveOrEditModal,
          currentProductSize: ''
        })
      })
    }

    const deleteProductSize = (productSizeId) => {
      this.setState({tempProductSizeId: productSizeId, showDeletingModal: !this.state.showDeletingModal})
    }
    const toggleProductSizeDeleteModal = () => {
      this.setState({tempProductSizeId: '', showDeletingModal: !this.state.showDeletingModal})
    }

    const konkretOchirish = () => {
      if (this.state.tempProductSizeId) {
        dispatch({
          type: 'productSize/deleteProductSize',
          payload: {
            id: this.state.tempProductSizeId
          }
        }).then(res => {
          dispatch({
            type: 'productSize/getProductSizeByPageable',
            payload: {
              page: 0,
              size: 10
            }
          });
          this.setState({showDeletingModal: !this.state.showDeletingModal})
        })
      }
    };
    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h1>Product Size page</h1>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
            onClick={toggleProductSizeSaveOrEditModal}>+</Button>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Name</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {productSizePage? productSizePage.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.name}</td>
                    <td>
                      <Button onClick={() => editProductSize(item)} color="warning">Edit</Button>

                      <Button onClick={() => deleteProductSize(item.id)} className="ml-3" color="danger">Delete</Button>
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
              totalItemsCount={productSizeTotalElements}
              pageRangeDisplayed={5}
              onChange={this.handlePageChange.bind(this)}itemClass="page-item"
              linkClass="page-link"
            />
          </Col>
        </Row>
        <Modal isOpen={this.state.showProductSizeSaveOrEditModal} toggle={toggleProductSizeSaveOrEditModal}>
          <ModalHeader
            toggle={this.modalControl}>{this.state.currentProductSize ? "O'lcham nomini o'zgartirish" : "Yangi o'lcham qo'shish"}</ModalHeader>
          <AvForm onValidSubmit={productSizeSaveOrEdit}>
            <ModalBody>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentProductSize ? this.state.currentProductSize.name : ''} className="mt-2"
                       placeholder="Name" name="name"/>
            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={toggleProductSizeSaveOrEditModal}>Bekor qilish</Button>

              <Button className="ml-3" color="success" type="submit">Saqlash</Button>
            </ModalFooter>
          </AvForm>
        </Modal>
        <Modal isOpen={this.state.showDeletingModal} toggle={toggleProductSizeDeleteModal}>
          <ModalHeader toggle={this.modalControl}>O'lchamni o'chirishni istaysizmi?</ModalHeader>

          <ModalBody>

          </ModalBody>
          <ModalFooter>
            <Button color="info" onClick={toggleProductSizeDeleteModal}>Bekor qilish</Button>
            <Button className="ml-3" color="danger" onClick={konkretOchirish} type="button">O'chirish</Button>
          </ModalFooter>
        </Modal>
      </div>

    );
  }
}

Index.propTypes = {};

export default Index;
