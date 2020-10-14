import React, {Component} from 'react';
import {connect} from "react-redux";
import {
  Button,
  Card,
  CardBody,
  CardSubtitle,
  CardText,
  CardTitle,
  Col, Modal,
  ModalBody, ModalFooter,
  ModalHeader,
  Row,
  Table
} from "reactstrap";
import {YMaps, Map} from 'react-yandex-maps'
import {AvField, AvForm} from "availity-reactstrap-validation";

@connect(({app, order, output}) => ({app, order, output}))
class Index extends Component {

  constructor(props) {
    super(props);
    this.state = {
      showOrderSaveOrEditModal: false,
      currentOrder: '',
      tempNewOrderPage: 1,
      tempApprovedOrderPage: 1,
      tempSendOrderPage: 1,
      tempRecievedOrderPage: 1,
      lan: '',
      lat: '',
      showLocationModal: false,
      reqOrder: {
        id: '',
        reqProductWithAmountList: [
          {
            productList: [],
            productId: '',
            sizeList: [],
            sizeId: '',
            amount: 0,
            catId: '',
            brandId: ''
          }
        ],
        customerId: '',
        customerFirstName: '',
        customerLastName: '',
        customerPhoneNumber: '',
        orderAddress: ''
      }
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    console.log(this.props, "<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>")
    dispatch({
      type: 'order/getAllOrdersByStatus',
      payload: {
        page: 0,
        size: 2,
        orderStatus: "NEW"
      }
    })
    dispatch({
      type: 'order/getAllOrdersByStatus',
      payload: {
        page: 0,
        size: 2,
        orderStatus: "APPROVED"
      }
    })
    dispatch({
      type: 'order/getAllOrdersByStatus',
      payload: {
        page: 0,
        size: 2,
        orderStatus: "SEND"
      }
    })
    dispatch({
      type: 'order/getAllOrdersByStatus',
      payload: {
        page: 0,
        size: 2,
        orderStatus: "RECIEVED"
      }
    })
    dispatch({
      type: 'app/getAllCategories'
    })
    dispatch({
      type: 'app/getAllBrands'
    })
    dispatch({
      type: 'app/getAllPayTypes',
      payload: {
        active: true
      }
    })
  }

  render() {
    const {dispatch, order, app} = this.props
    const {allWarehouseListWithoutOutputer, allCategories, allBrands, allPayTypes} = app
    const {
      allRecievedOrders, allSendOrders, allApprovedOrders,
      allNewOrders, recievedTotal, sendTotal, approvedTotal, newTotal, allProductsByCatAndBrand, selectedProduct, sizeListByProduct
    } = order


    const getCatId = (e, index) => {
      let tempCatId = e.target.value
      this.setState({tempCatId: e.target.value})
      if (this.state.tempBrandId) {
        dispatch({
          type: 'order/getAllProductsByCatAndBrand',
          payload: {
            catId: tempCatId,
            brandId: this.state.tempBrandId
          }
        }).then(res => {
          this.state.reqOrder.reqProductWithAmountList[index].productList = res.object
          this.state.reqOrder.reqProductWithAmountList[index].catId = tempCatId
          this.setState(this.state)
        })
      } else {
        dispatch({
          type: 'order/getAllProductsByCatAndBrand',
          payload: {
            catId: tempCatId,
            brandId: null
          }
        }).then(res => {
          this.state.reqOrder.reqProductWithAmountList[index].productList = res.object
          this.state.reqOrder.reqProductWithAmountList[index].catId = tempCatId
          this.setState(this.state)
        })
      }
    }
    const getBrandId = (e, index) => {
      let tempBrandId = e.target.value
      this.setState({tempBrandId: e.target.value})
      if (this.state.tempCatId) {
        dispatch({
          type: 'order/getAllProductsByCatAndBrand',
          payload: {
            catId: this.state.tempCatId,
            brandId: tempBrandId
          }
        }).then(res => {
          this.state.reqOrder.reqProductWithAmountList[index].productList = res.object
          this.state.reqOrder.reqProductWithAmountList[index].brandId = tempBrandId
          this.setState(this.state)
        })
      } else {
        dispatch({
          type: 'order/getAllProductsByCatAndBrand',
          payload: {
            brandId: tempBrandId,
            catId: null
          }
        }).then(res => {
          this.state.reqOrder.reqProductWithAmountList[index].productList = res.object
          this.state.reqOrder.reqProductWithAmountList[index].brandId = tempBrandId
          this.setState(this.state)
        })
      }
    }
    const getProductId = (e, index) => {
      let tempId = e.target.value
      dispatch({
        type: 'order/getProductSizeListByProduct',
        payload: {
          path: tempId
        }
      }).then(res => {
        this.state.reqOrder.reqProductWithAmountList[index].sizeList = res.object.productSizeList
        this.state.reqOrder.reqProductWithAmountList[index].productId = tempId
        this.setState(this.state)
      })
    }
    const getSize = (e, index) => {
      this.state.reqOrder.reqProductWithAmountList[index].sizeId = e.target.value
      this.setState(this.state)
    }
    const getAmount = (e, index) => {
      this.state.reqOrder.reqProductWithAmountList[index].amount = e.target.value
      this.setState(this.state)
    }
    const removeItem = (ind) => {
      delete this.state.reqOrder.reqProductWithAmountList[ind]
      this.setState(this.state)
    }

    const addNewRow = () => {
      let s = {
        productList: [],
        productId: '',
        sizeList: [],
        sizeId: '',
        amount: 0,
        catId: '',
        brandId: ''
      }

      // let temp = this.state.reqProductWithAmountList
      this.state.reqOrder.reqProductWithAmountList.push(s)
      this.setState(this.state)
    }

    const toggleOrderSaveOrEditModal = () => {
      this.setState({
        showOrderSaveOrEditModal: !this.state.showOrderSaveOrEditModal,
        currentOrder: ''
      })
    }
    const getNext = (status) => {
      let s = 0
      if (status === 'NEW') {
        s = this.state.tempNewOrderPage
        this.setState({tempNewOrderPage: this.state.tempNewOrderPage + 1})
      }
      if (status === 'APPROVED') {
        s = this.state.tempApprovedOrderPage
        this.setState({tempApprovedOrderPage: this.state.tempApprovedOrderPage + 1})
      }
      if (status === 'SEND') {
        s = this.state.tempSendOrderPage
        this.setState({tempSendOrderPage: this.state.tempSendOrderPage + 1})
      }
      if (status === 'RECIEVED') {
        s = this.state.tempRecievedOrderPage
        this.setState({tempRecievedOrderPage: this.state.tempRecievedOrderPage + 1})
      }
      dispatch({
        type: 'order/getAllOrdersByStatusSecond',
        payload: {
          page: s,
          size: 2,
          orderStatus: status
        }
      }).then(res => {
        let tempArray = []
        if (status === 'NEW') {
          tempArray = allNewOrders.concat(res.object)
          dispatch({
            type: 'order/updateState',
            payload: {
              allNewOrders: tempArray
            }
          })
        }
        if (status === 'APPROVED') {
          tempArray = allApprovedOrders.concat(res.object)
          dispatch({
            type: 'order/updateState',
            payload: {
              allApprovedOrders: tempArray
            }
          })
        }
        if (status === 'SEND') {
          tempArray = allSendOrders.concat(res.object)
          dispatch({
            type: 'order/updateState',
            payload: {
              allSendOrders: tempArray
            }
          })
        }
        if (status === 'RECIEVED') {
          tempArray = allRecievedOrders.concat(res.object)
          dispatch({
            type: 'order/updateState',
            payload: {
              allRecievedOrders: tempArray
            }
          })
        }

      })
    }
    const changeOrderStatus = (id, status) => {
      dispatch({
        type: 'order/changeOrderStatus',
        payload: {
          id,
          orderStatus: status
        }
      }).then(res => {
        if (res.success) {
          dispatch({
            type: 'order/getAllOrdersByStatus',
            payload: {
              page: 0,
              size: 2,
              orderStatus: "NEW"
            }
          })
          dispatch({
            type: 'order/getAllOrdersByStatus',
            payload: {
              page: 0,
              size: 2,
              orderStatus: "APPROVED"
            }
          })
          dispatch({
            type: 'order/getAllOrdersByStatus',
            payload: {
              page: 0,
              size: 2,
              orderStatus: "SEND"
            }
          })
          dispatch({
            type: 'order/getAllOrdersByStatus',
            payload: {
              page: 0,
              size: 2,
              orderStatus: "RECIEVED"
            }
          })
        }
      })
    }
    const showLocation = (lan, lat) => {
      this.setState({lan, lat, showLocationModal: !this.state.showLocationModal})
    }
    const closeShowLocationModal = () => {
      this.setState({lan: '', lat: '', showLocationModal: !this.state.showLocationModal})
    }
    const orderSaveOrEdit = (e, v) => {
      let reqProductWithAmountList = [];
      Object.keys(v).forEach(inputName => {
        if (inputName.split('/')[0] === 'productId') {
          reqProductWithAmountList[inputName.split('/')[1]] = {
            productId: v[inputName],
            amount: v['amount/' + inputName.split('/')[1]],
            size: v['productSizeId/' + inputName.split('/')[1]]
          }
        }
      });
      var filtered = reqProductWithAmountList.filter(Boolean);
      let s = {
        customerFirstName: v.clientFirstName,
        customerLastName: v.clientLastName,
        customerPhoneNumber: v.clientPhoneNumber,
        customerAddress: v.clientAddress,
        orderAddress: v.orderAddress,
        reqProductWithAmountList: filtered,
        payStatus: 'UNPAID',
        orderStatus: 'APPROVED',
        payTypeId: v.payTypeId
      }
      if (this.state.currentOutput) {
        s = {...s, id: this.state.currentOutput.id}
      }
      console.log(s, "S===VAlues")
      dispatch({
        type: 'order/saveOrder',
        payload: s
      }).then(res => {
        dispatch({
          type: 'order/getAllOrdersByStatus',
          payload: {
            page: 0,
            size: 2,
            orderStatus: "APPROVED"
          }
        })
        toggleOrderSaveOrEditModal()
      })
    }
    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h3>Buyurtmalar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleOrderSaveOrEditModal}>+</Button>
          </Col>
        </Row>
        <Row className="mt-3">
          <Col>
            <h3 className="mb-3 text-center">Yangi buyurtmalar</h3>
            {allNewOrders ? allNewOrders.map((item, index) =>
              <Card>
                <CardBody>
                  <CardTitle>
                    <span>{item.createdAt.substr(0, 10)}</span><br/>
                    <span>{item.customer.firstName ? item.customer.firstName : ''}</span>
                    <span>{item.customer.lastName ? item.customer.lastName : ''}</span>
                    <span>{item.customer.phoneNumber ? item.customer.phoneNumber : ''}</span>
                  </CardTitle>
                  <CardText>
                    {item.resProductWithAmountList ? item.resProductWithAmountList.map((item, index) =>
                      <div>
                        <span>{index + 1}</span>
                        <span className="ml-2">{item.resProduct.nameUz}</span>
                        <span className="ml-2">{item.productSize.name}</span>
                        <span className="ml-2">{item.amount}</span>
                      </div>
                    ) : ''}
                    <div className="mt-2">
                      <span>Address:{item.orderAddress ? item.orderAddress : <Button type="button" color="info"
                                                                                     onClick={() => showLocation(item.lan, item.lat)}>Location</Button>}</span>
                    </div>
                  </CardText>
                  <Button type="button" color="success"
                          onClick={() => changeOrderStatus(item.id, 'APPROVED')}>Tasdiqlash</Button>
                </CardBody>
              </Card>
            ) : ''
            }

            {console.log(allNewOrders.length, "SIZE")}
            {console.log(newTotal, "total")}
            {allNewOrders.length < newTotal ?
              <div>
                <Button className="ml-5" type="button" color="warning" onClick={() => getNext('NEW')}>Yana
                  ko'rish</Button>
              </div>
              : ''
            }
          </Col>
          <Col>
            <h3 className="mb-3 text-center">Tasdiqlangan buyurtmalar</h3>
            {allApprovedOrders ? allApprovedOrders.map((item, index) =>
              <Card>
                <CardBody>
                  <CardTitle>
                    <span>{item.createdAt.substr(0, 10)}</span><br/>
                    <span>{item.customer.firstName ? item.customer.firstName : ''}</span>
                    <span>{item.customer.lastName ? item.customer.lastName : ''}</span>
                    <span>{item.customer.phoneNumber ? item.customer.phoneNumber : ''}</span>
                  </CardTitle>
                  <CardText>
                    {item.resProductWithAmountList ? item.resProductWithAmountList.map((item, index) =>
                      <div>
                        <span>{index + 1}</span>
                        <span className="ml-2">{item.resProduct.nameUz}</span>
                        <span className="ml-2">{item.productSize.name}</span>
                        <span className="ml-2">{item.amount}</span>
                      </div>
                    ) : ''}
                    <div className="mt-2">
                      <span>Address:{item.orderAddress ? item.orderAddress : <Button type="button" color="info"
                                                                                     onClick={() => showLocation(item.lan, item.lat)}>Location</Button>}</span>
                    </div>
                  </CardText>
                </CardBody>
              </Card>
            ) : ''
            }
            {allApprovedOrders.length < approvedTotal ?
              <div>
                <Button className="ml-5" type="button" color="warning" onClick={() => getNext('APPROVED')}>Yana
                  ko'rish</Button>
              </div>
              : ''
            }
          </Col>
          <Col>
            <h3 className="mb-3 text-center">Jo'natilgan buyurtmalar</h3>
            {allSendOrders ? allSendOrders.map((item, index) =>
              <Card>
                <CardBody>
                  <CardTitle>
                    <span>{item.createdAt.substr(0, 10)}</span><br/>
                    <span>{item.customer.firstName ? item.customer.firstName : ''}</span>
                    <span>{item.customer.lastName ? item.customer.lastName : ''}</span>
                    <span>{item.customer.phoneNumber ? item.customer.phoneNumber : ''}</span>
                  </CardTitle>
                  <CardText>
                    {item.resProductWithAmountList ? item.resProductWithAmountList.map((item, index) =>
                      <div>
                        <span>{index + 1}</span>
                        <span className="ml-2">{item.resProduct.nameUz}</span>
                        <span className="ml-2">{item.productSize.name}</span>
                        <span className="ml-2">{item.amount}</span>
                      </div>
                    ) : ''}
                    <div className="mt-2">
                      <span>Address:{item.orderAddress ? item.orderAddress : <Button type="button" color="info"
                                                                                     onClick={() => showLocation(item.lan, item.lat)}>Location</Button>}</span>
                    </div>
                  </CardText>
                  <Button type="button" color="success" onClick={() => changeOrderStatus(item.id, 'RECIEVED')}>Qabul
                    qilingan</Button>
                </CardBody>
              </Card>
            ) : ''
            }
            {allSendOrders.length < sendTotal ?
              <div>
                <Button className="ml-5" type="button" color="warning" onClick={() => getNext('SEND')}>Yana
                  ko'rish</Button>
              </div>
              : ''
            }
          </Col>
          <Col>
            <h3 className="mb-3 text-center">Qabul qilingan buyurtmalar</h3>
            {allRecievedOrders ? allRecievedOrders.map((item, index) =>
              <Card>
                <CardBody>
                  <CardTitle>
                    <span>{item.createdAt.substr(0, 10)}</span><br/>
                    <span>{item.customer.firstName ? item.customer.firstName : ''}</span>
                    <span>{item.customer.lastName ? item.customer.lastName : ''}</span>
                    <span>{item.customer.phoneNumber ? item.customer.phoneNumber : ''}</span>
                  </CardTitle>
                  <CardText>
                    {item.resProductWithAmountList ? item.resProductWithAmountList.map((item, index) =>
                      <div>
                        <span>{index + 1}</span>
                        <span className="ml-2">{item.resProduct.nameUz}</span>
                        <span className="ml-2">{item.productSize.name}</span>
                        <span className="ml-2">{item.amount}</span>
                      </div>
                    ) : ''}
                    <div className="mt-2">
                      <span>Address:{item.orderAddress ? item.orderAddress : <Button type="button" color="info"
                                                                                     onClick={() => showLocation(item.lan, item.lat)}>Location</Button>}</span>
                    </div>
                  </CardText>
                </CardBody>
              </Card>
            ) : ''
            }
            {allRecievedOrders.length < recievedTotal ?
              <div>
                <Button className="ml-5" type="button" color="warning" onClick={() => getNext('RECIEVED')}>Yana
                  ko'rish</Button>
              </div>
              : ''
            }
          </Col>
        </Row>
        <Modal isOpen={this.state.showLocationModal} toggle={closeShowLocationModal}>
          <ModalHeader>Buyurtma lokatsiyasi</ModalHeader>
          <ModalBody>
            <YMaps>
              <div>
                My awesome application with maps!
                <Map defaultState={{center: [this.state.lat, this.state.lan], zoom: 15}}/>
              </div>
            </YMaps>
          </ModalBody>
          <ModalFooter>
            <Button className="ml-3" color="info" type="button" onClick={closeShowLocationModal}>Ok</Button>
          </ModalFooter>
        </Modal>


        <Modal size="lg" style={{maxWidth: '1600px', width: '80%'}} isOpen={this.state.showOrderSaveOrEditModal}
               toggle={toggleOrderSaveOrEditModal}>
          <ModalHeader>
            {this.state.currentOrder ? "Editing 📝" : "Add new Order 🖊"}
          </ModalHeader>
          <AvForm onValidSubmit={orderSaveOrEdit}>
            <ModalBody>
              {this.state.reqOrder ?
                <Row>
                  <Col>
                    <Row>
                      <Col md={{size: 2}}>
                        <AvField required={true} type="text"
                                 defaultValue={this.state.currentOrder ? this.state.currentRegionSize.nameUz : ''}
                                 className="mt-2" placeholder="Enter Firstname " name="clientFirstName"/>
                      </Col>
                      <Col md={{size: 2}}>
                        <AvField required={true} type="text"
                                 defaultValue={this.state.currentOrder ? this.state.currentRegionSize.nameUz : ''}
                                 className="mt-2" placeholder="Enter LastName" name="clientLastName"/>
                      </Col>
                      <Col md={{size: 2}}>
                        <AvField required={true} type="text"
                                 defaultValue={this.state.currentOrder ? this.state.currentRegionSize.nameUz : ''}
                                 className="mt-2" placeholder="Enter Phone number " name="clientPhoneNumber"/>
                      </Col>
                      <Col md={{size: 2}}>
                        <AvField required={true} type="text"
                                 defaultValue={this.state.currentOrder ? this.state.currentRegionSize.nameUz : ''}
                                 className="mt-2" placeholder="Enter Adress" name="clientAddress"/>
                      </Col>
                      <Col md={{size: 2}}>
                        <AvField required={true} type="text"
                                 defaultValue={this.state.currentOrder ? this.state.currentRegionSize.nameUz : ''}
                                 className="mt-2" placeholder="Enter Order Adress" name="orderAddress"/>
                      </Col>
                      <Col md={{size: 2}}>
                        <AvField type="select" className="mt-2" placeholder="To'lov turini tanlang"
                                 name="payTypeId">
                          <option>To'lov turini tanlang</option>
                          {allPayTypes ? allPayTypes.map(item =>
                            <option value={item.id}>{item.nameUz}</option>
                          ) : ''}
                        </AvField>
                      </Col>

                      {/*<AvField type="select" className="mt-2" placeholder="To'lov turini tanlang" id={`payTypeId${index}`}*/}
                      {/*         name={`payTypeId/${index}`}*/}
                      {/*         onChange={(e) => getCatId(e, index)} defaultValue={item.catId}>*/}
                      {/*  <option>Kategoriyani tanlang</option>*/}
                      {/*  {allRecievedOrders.payType ? allRecievedOrders.payType.map(item =>*/}
                      {/*    <option value={item.id}>{item.payTypeId}</option>*/}
                      {/*  ) : ''}*/}
                      {/*</AvField>*/}
                      <Col></Col>
                      <Col></Col>
                      <Col></Col>
                      <Col></Col>
                    </Row>

                    {this.state.reqOrder.reqProductWithAmountList ? this.state.reqOrder.reqProductWithAmountList.map((item, index) =>
                      <Row>
                        <Col>
                          <AvField type="select" className="mt-2" placeholder="Kategoriya tanlang" id={`catId${index}`}
                                   name={`catId/${index}`}
                                   onChange={(e) => getCatId(e, index)} defaultValue={item.catId}>
                            <option>Kategoriyani tanlang</option>
                            {allCategories ? allCategories.map(item =>
                              <option value={item.id}>{item.nameUz}</option>
                            ) : ''}
                          </AvField>
                        </Col>
                        <Col>
                          <AvField type="select" className="mt-2"
                                   placeholder="Brand tanlang" id={`brandId${index}`}
                                   name={`brandId/${index}`}
                                   onChange={(e) => getBrandId(e, index)}
                                   defaultValue={item.brandId}>
                            <option>Brand tanlang</option>
                            {allBrands ? allBrands.map(item =>
                              <option value={item.id}>{item.nameUz}</option>
                            ) : ''}
                          </AvField>
                        </Col>
                        <Col>
                          <AvField type="select" className="mt-2" placeholder="Maxsulot tanlang"
                                   id={`productId${index}`}
                                   name={`productId/${index}`}
                                   onChange={(e) => getProductId(e, index)} defaultValue={item.productId}>
                            <option>Mahsulot tanlang</option>
                            {item.productList ? item.productList.map(item =>
                              <option value={item.id}>{item.nameUz}</option>
                            ) : ''}
                          </AvField>
                        </Col>
                        <Col>
                          <AvField type="select" className="mt-2" placeholder="Razmer tanlang"
                                   id={`productSizeId${index}`} name={`productSizeId/${index}`}
                                   onChange={(e) => getSize(e, index)} defaultValue={item.sizeId}>
                            <option>Razmer tanlang</option>
                            {item.sizeList ? item.sizeList.map(item =>
                              <option value={item.id}>{item.name}</option>
                            ) : ''}
                          </AvField>
                        </Col>
                        <Col>
                          <AvField required={true} defaultValue={item.amount} type="number" className="mt-2"
                                   placeholder="Soni" id={`amount${index}`} name={`amount/${index}`}
                                   onChange={(e) => getAmount(e, index)}/>
                        </Col>
                        <Col>
                          <Button className="ml-5" color="danger" style={{borderRadius: "20px"}}
                                  onClick={() => removeItem(index)}>-</Button>
                        </Col>
                      </Row>
                    ) : ''}
                  </Col>
                </Row> : ''}
              <Row>
                <Col md={{size: 2, offset: 5}}>
                  <Button className="ml-5" color="info" style={{borderRadius: "20px"}}
                          onClick={addNewRow}>+</Button>
                </Col>
              </Row>
            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={toggleOrderSaveOrEditModal}>Bekor qilish</Button>
              <Button className="ml-3" color="success" type="submit">Saqlash</Button>
            </ModalFooter>
          </AvForm>
        </Modal>
      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
