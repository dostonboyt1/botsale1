import React, {Component} from 'react';
import {Button, CardTitle, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {connect} from "react-redux";

@connect(({app, warehouseOrder}) => ({app, warehouseOrder}))
class Index extends Component {

  constructor(props) {
    super(props);
    this.state = {
      isNew: true,
      productWithAmountList: [],
      showInputProductWithAmountModal: false
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'warehouseOrder/getApprovedOrders',
      payload: {
        page: 0,
        size: 10,
        orderStatus: "APPROVED"
      }
    })
  }

  render() {
    const {dispatch, app, warehouseOrder} = this.props
    const {totalSend, sendOrders, totalApproved, approvedOrders} = warehouseOrder

    const getOrdersByStatus = (e) => {
      let s = e.target.value
      if (s === 'APPROVED') {
        this.setState({isNew: true})
        dispatch({
          type: 'warehouseOrder/getApprovedOrders',
          payload: {
            page: 0,
            size: 10,
            orderStatus: "APPROVED"
          }
        })
      }
      if (s === 'SEND') {
        this.setState({isNew: false})
        dispatch({
          type: 'warehouseOrder/getSendOrders',
          payload: {
            page: 0,
            size: 10
          }
        })
      }
    }

    const showInputProductWithAmountList = (productList) => {
      this.setState({
        productWithAmountList: productList,
        showInputProductWithAmountModal: !this.state.showInputProductWithAmountModal
      })
    }
    const closeShowProductModal = () => {
      this.setState({
        productWithAmountList: [],
        showInputProductWithAmountModal: !this.state.showInputProductWithAmountModal
      })
    }

    const sendOrder = (id) => {
      dispatch({
        type:'warehouseOrder/changeToSend',
        payload:{
          path:id
        }
      }).then(res=>{
        dispatch({
          type: 'warehouseOrder/getApprovedOrders',
          payload: {
            page: 0,
            size: 10,
            orderStatus: "APPROVED"
          }
        })
      })
    }
    return (
      <div>
        <Row>
          <Col>
            <h3>Buyurtmalar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <select name="orderStatus" onChange={getOrdersByStatus}>
              <option value="APPROVED">Yangi buyurtmalar</option>
              <option value="SEND">Jo'natilgan buyurtmalar</option>
            </select>
          </Col>
        </Row>

        <Row>
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Mijoz Info</th>
                <th>Maxsulotlar</th>
                <th>Holati</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {this.state.isNew ?
                approvedOrders ? approvedOrders.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td><span>{item.customer.firstName ? item.customer.firstName : ''}</span>
                      <span>{item.customer.lastName ? item.customer.lastName : ''}</span>
                      <span>{item.customer.phoneNumber ? item.customer.phoneNumber : ''}</span></td>
                    <td><Button type="button"
                                onClick={() => showInputProductWithAmountList(item.resProductWithAmountList)}>...</Button>
                    </td>
                    <td><input type="checkbox" checked={false}/></td>
                    <td><Button type="button" color="success" onClick={() => sendOrder(item.id)}>Jonatish</Button></td>
                  </tr>
                ) : ''
                :
                sendOrders ? sendOrders.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td><span>{item.customer.firstName ? item.customer.firstName : ''}</span>
                      <span>{item.customer.lastName ? item.customer.lastName : ''}</span>
                      <span>{item.customer.phoneNumber ? item.customer.phoneNumber : ''}</span></td>
                    <td><Button type="button"
                                onClick={() => showInputProductWithAmountList(item.resProductWithAmountList)}>...</Button>
                    </td>
                    <td><input type="checkbox" checked={true}/></td>
                  </tr>
                ) : ''
              }
              </tbody>
            </Table>
          </Col>
        </Row>
        <Modal isOpen={this.state.showInputProductWithAmountModal} toggle={closeShowProductModal}>
          <ModalHeader>Kirim bo'lgan mahsulotlar ro'yxati</ModalHeader>
          <ModalBody>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Nomi</th>
                <th>Razmeri</th>
                <th>Soni</th>
              </tr>
              </thead>
              <tbody>
              {this.state.productWithAmountList ? this.state.productWithAmountList.map((item, index) =>
                <tr>
                  <td>{index + 1}</td>
                  <td>{item.resProduct.nameUz}</td>
                  <td>{item.productSize.name}</td>
                  <td>{item.amount}</td>
                </tr>
              ) : ''}

              </tbody>
            </Table>
          </ModalBody>
          <ModalFooter>
            <Button className="ml-3" color="info" type="button" onClick={closeShowProductModal}>Ok</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
