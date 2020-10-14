import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import {connect} from "react-redux";
import Pagination from "react-js-pagination";

@connect(({app, output}) => ({app, output}))
class Index extends Component {

  constructor(props) {
    super(props);
    this.state = {
      showOutputSaveOrEditModal: false,
      currentOutput: '',
      tempCatId: '',
      tempBrandId: '',
      productWithAmountList:[],
      activePage: 1,
      showOutputProductWithAmountListModal: false,
      reqOutput: {
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
        incomerWarehouseId: ''
      }
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'app/getAllWarehouseWithoutOutputer'
    })
    dispatch({
      type: 'app/getAllCategories'
    })
    dispatch({
      type: 'app/getAllBrands'
    })
    dispatch({
      type:'output/getAllOutputByPageable',
      payload:{
        page:0,
        size:10,
        confirmed:false,
        isIncome:false
      }
    })
  }
  handlePageChange(pageNumber) {
    const {dispatch} = this.props
    console.log(`active page is ${pageNumber}`);
    this.setState({activePage: pageNumber});
    dispatch({
      type: 'app/getAllWarehouseWithoutOutputer',
      payload: {
        page: pageNumber - 1,
        size: 10
      }
    })

  }

  render() {
    const {dispatch, app, output} = this.props
    const {allWarehouseListWithoutOutputer, allCategories, allBrands} = app
    const {allOutputByPageable,allOutputTotalElement}=output
    console.log(allWarehouseListWithoutOutputer, "ALL Warehouse List Without Outputer")

    const toggleOutputSaveOrEditModal = () => {
      this.setState({
        showOutputSaveOrEditModal: !this.state.showOutputSaveOrEditModal,
        currentOutput: ''
      })
    }
    const outputSaveOrEdit = (e, v) => {
      let reqProductWithAmountList = [];
      Object.keys(v).forEach(inputName => {
        if (inputName.split('/')[0] === 'productId') {
          reqProductWithAmountList[inputName.split('/')[1]]={
            productId: v[inputName],
            amount: v['amount/' + inputName.split('/')[1]],
            size: v['productSizeId/' + inputName.split('/')[1]]
          }
        }
      });
      var filtered = reqProductWithAmountList.filter(Boolean);
      let s={
        incomerWarehouseId: v.incomerWarehouseId,
        reqProductWithAmountList:filtered
      }
      if (this.state.currentOutput){
        s={...s,id:this.state.currentOutput.id}
      }
      console.log(s,"S===VAlues")
      dispatch({
        type:'output/saveOrEditOutput',
        payload:s
      }).then(res=>{
        dispatch({
          type:'output/getAllOutputByPageable',
          payload:{
            page:0,
            size:10,
            confirmed:false,
            isIncome:false
          }
        })
        toggleOutputSaveOrEditModal()
      })
    }
    const addNewRow = () => {
      let s =  {
        productList: [],
        productId:'',
        sizeList: [],
        sizeId:'',
        amount: 0,
        catId:'',
        brandId:''
      }
      // let temp = this.state.reqProductWithAmountList
      this.state.reqOutput.reqProductWithAmountList.push(s)
      this.setState(this.state)
    }
    const closeShowProductModal = () => {
      this.setState({
        showOutputProductWithAmountListModal: !this.state.showOutputProductWithAmountListModal
      })
    }

    const getIncomerWarehouse = (e) => {
      console.log(e.target.value, "INCOMERID")
    }

    const getCatId = (e, index) => {
      let tempCatId=e.target.value
      this.setState({tempCatId: e.target.value})
      if (this.state.tempBrandId) {
        dispatch({
          type: 'output/getAllProductsByCatAndBrand',
          payload: {
            catId: tempCatId,
            brandId: this.state.tempBrandId
          }
        }).then(res=>{
          this.state.reqOutput.reqProductWithAmountList[index].productList=res.object
          this.state.reqOutput.reqProductWithAmountList[index].catId=tempCatId
          this.setState(this.state)
        })
      } else {
        dispatch({
          type: 'output/getAllProductsByCatAndBrand',
          payload: {
            catId: tempCatId,
            brandId: null
          }
        }).then(res=>{
          this.state.reqOutput.reqProductWithAmountList[index].productList=res.object
          this.state.reqOutput.reqProductWithAmountList[index].catId=tempCatId
          this.setState(this.state)
        })
      }
    }
    const getBrandId = (e, index) => {
      let tempBrandId=e.target.value
      this.setState({tempBrandId: e.target.value})
      if (this.state.tempCatId) {
        dispatch({
          type: 'output/getAllProductsByCatAndBrand',
          payload: {
            catId: this.state.tempCatId,
            brandId: tempBrandId
          }
        }).then(res=>{
          this.state.reqOutput.reqProductWithAmountList[index].productList=res.object
          this.state.reqOutput.reqProductWithAmountList[index].brandId=tempBrandId
          this.setState(this.state)
        })
      } else {
        dispatch({
          type: 'output/getAllProductsByCatAndBrand',
          payload: {
            brandId: tempBrandId,
            catId: null
          }
        }).then(res=>{
          this.state.reqOutput.reqProductWithAmountList[index].productList=res.object
          this.state.reqOutput.reqProductWithAmountList[index].brandId=tempBrandId
          this.setState(this.state)
        })
      }
    }
    const getProductId = (e, index) => {
      let tempId=e.target.value
      dispatch({
        type: 'output/getProductSizeListByProduct',
        payload: {
          path:tempId
        }
      }).then(res=>{
        this.state.reqOutput.reqProductWithAmountList[index].sizeList=res.object.productSizeList
        this.state.reqOutput.reqProductWithAmountList[index].productId=tempId
        this.setState(this.state)
      })
    }
    const getSize = (e, index) => {
      this.state.reqOutput.reqProductWithAmountList[index].sizeId= e.target.value
      this.setState(this.state)
    }
    const getAmount = (e, index) => {
      this.state.reqOutput.reqProductWithAmountList[index].amount= e.target.value
      this.setState(this.state)
    }
    const removeItem = (ind) => {
      delete this.state.reqOutput.reqProductWithAmountList[ind]
      this.setState(this.state)
    }

    const showOutputProductWithAmountList=(productList)=>{
      this.setState({productWithAmountList:productList,showOutputProductWithAmountListModal:!this.state.showOutputProductWithAmountListModal})
    }

    const removeInput=(id)=>{
      dispatch({
        type:'output/removeInputOrOutput',
        payload:{
          id
        }
      }).then(res=>{
        dispatch({
          type:'output/getAllOutputByPageable',
          payload:{
            page:0,
            size:10,
            confirmed:false,
            isIncome:false
          }
        })
      })
    }

    const editInput=(item)=>{
      var tempArray = item.resProductWithAmountList.map(myFunction)

      function myFunction(item) {
        return {
          id:item.id,
          productList: item.productListByCategoryAndBrand,
          productId: item.resProduct.id,
          sizeList: item.resProduct.productSizeList,
          sizeId: item.productSize.id,
          amount: item.amount,
          catId: item.resProduct.resCategory.id,
          brandId: item.resProduct.brand.id
        };
      }
      let s={
        id: item.id,
        reqProductWithAmountList:tempArray,
        incomerWarehouseId: item.incomer.id

      }
      this.setState({
        showInputSaveOrEditModal: !this.state.showInputSaveOrEditModal,
        currentInput: item,
        reqOutput:s
      })
    }

    const getByConfirm=(e)=>{
      dispatch({
        type:'output/getAllOutputByPageable',
        payload:{
          page:0,
          size:10,
          confirmed:e.target.value,
          isIncome:false
        }
      })

    }

    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h3>Chiqimlar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleOutputSaveOrEditModal}>+</Button>
          </Col>
          <Col></Col>
          <Col><select name="confirm" onChange={getByConfirm}>
            <option value={false}>Tasdiqlanmagan</option>
            <option value={true}>Tasdiqlangan</option>
          </select></Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Sana</th>
                <th>Qaerga</th>
                <th>Maxsulotlar</th>
                <th>Holati</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {allOutputByPageable?allOutputByPageable.map((item,index)=>
                <tr>
                  <td>{index+1}</td>
                  <td>{item.createdAt.substr(0,10)}</td>
                  <td>{item.incomer!=null?item.incomer.name:''}</td>
                  <td><Button type="button" onClick={()=>showOutputProductWithAmountList(item.resProductWithAmountList)}>...</Button></td>
                  <td><input type="checkbox" checked={item.confirmed}/></td>
                  <td>{item.confirmed?'':<div>
                    <Button className="mr-2" type="button" color="warning" onClick={()=>editInput(item)}>Edit</Button>
                    <Button type="button" color="danger" className="ml-2" onClick={()=>removeInput(item.id)}>Delete</Button>
                  </div>}</td>
                </tr>
              ):''}
              </tbody>
            </Table>
          </Col>
        </Row>
        <Row>
          <Col>
            <Pagination
              activePage={this.state.activePage}
              itemsCountPerPage={10}
              totalItemsCount={allOutputTotalElement}
              pageRangeDisplayed={5}
              onChange={this.handlePageChange.bind(this)}
              itemClass="page-item"
              linkClass="page-link"
            />
          </Col>
        </Row>

        <Modal size="lg" style={{maxWidth: '1600px', width: '80%'}} isOpen={this.state.showOutputSaveOrEditModal}
               toggle={toggleOutputSaveOrEditModal}>
          <ModalHeader>Kirim qilish</ModalHeader>
          <AvForm onValidSubmit={outputSaveOrEdit}>
            <ModalBody>

               {this.state.reqOutput ?
                <Row>
                  <Col>
                    <Row>
                      <Col>

                      </Col>
                      <Col></Col>
                      <Col></Col>
                      <Col></Col>
                      <Col></Col>
                    </Row>

                    {this.state.reqOutput.reqProductWithAmountList ? this.state.reqOutput.reqProductWithAmountList.map((item, index) =>
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
                          <AvField type="select" className="mt-2" placeholder="Brand tanlang" id={`brandId${index}`}
                                   name={`brandId/${index}`}
                                   onChange={(e) => getBrandId(e, index)} defaultValue={item.brandId}>
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

              {/*<Row className="mt-2">*/}
              {/*  <Col>*/}
              {/*    <Table>*/}
              {/*      <thead>*/}
              {/*      <tr>*/}
              {/*        <th>T/R</th>*/}
              {/*        <th>Maxsulot</th>*/}
              {/*        <th>Size</th>*/}
              {/*        <th>Amallar</th>*/}
              {/*      </tr>*/}
              {/*      </thead>*/}
              {/*      <tbody>*/}
              {/*      {this.state.reqProductWithAmountList?this.state.reqProductWithAmountList.map((item,index)=>*/}
              {/*        <tr>*/}
              {/*          <td key={item.selectedProduct.id+index}>{index+1}</td>*/}
              {/*          <td key={item.selectedProduct.id+index}>{item.selectedProduct.nameUz}</td>*/}
              {/*          <td key={item.selectedProduct.id+index}>{item.selectedProduct.productSizeList.filter(itemm=>itemm.id===item.size?<span>itemm.name</span>:'')}</td>*/}
              {/*        </tr>*/}
              {/*        ):''}*/}
              {/*      </tbody>*/}
              {/*    </Table>*/}
              {/*  </Col>*/}
              {/*</Row>*/}
            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={toggleOutputSaveOrEditModal}>Bekor qilish</Button>
              <Button className="ml-3" color="success" type="submit">Saqlash</Button>
            </ModalFooter>
          </AvForm>
        </Modal>

        <Modal isOpen={this.state.showOutputProductWithAmountListModal} toggle={closeShowProductModal}>
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
              {this.state.productWithAmountList?this.state.productWithAmountList.map((item,index)=>
                <tr>
                  <td>{index+1}</td>
                  <td>{item.resProduct.nameUz}</td>
                  <td>{item.productSize.name}</td>
                  <td>{item.amount}</td>
                </tr>
              ):''}
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
