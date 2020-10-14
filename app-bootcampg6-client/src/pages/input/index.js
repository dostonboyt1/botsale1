import React, {Component} from 'react';
import {connect} from "react-redux";
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import Pagination from "react-js-pagination";

@connect(({input, app}) => ({input, app}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showInputSaveOrEditModal: false,
      currentInput: '',
      tempCatId: '',
      tempBrandId: '',
      activePage:1,
      reqProductWithAmountList: [
        {
          productList: [],
          productId:'',
          sizeList: [],
          sizeId:'',
          amount: 0,
          catId:'',
          brandId:''
        }],
    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type:'input/getAllInputsByPageable',
      payload:{
        page:0,
        size:10,
        confirmed:false,
        isIncome:true
      }
    })
    dispatch({
      type: 'app/getAllCategories'
    })
    dispatch({
      type: 'app/getAllBrands'
    })
  }

  handlePageChange(pageNumber) {
    const {dispatch} = this.props
    this.setState({activePage: pageNumber});
    dispatch({
      type:'input/getAllInputsByPageable',
      payload:{
        page:pageNumber-1,
        size:10,
        confirmed:this.state.tempConfirm,
        isIncome:true
      }
    })
  }

  render() {
    const {dispatch, input, app} = this.props
    const {allCategories, allBrands} = app
    const {allInputsByPageable,allInputsTotalElement} = input
    console.log(allInputsByPageable,"4564564566456")
    const toggleInputSaveOrEditModal = () => {
      this.setState({
        showInputSaveOrEditModal: !this.state.showInputSaveOrEditModal,
        currentInput: '',
        reqProductWithAmountList:[
          {
            productList: [],
            productId:'',
            sizeList: [],
            sizeId:'',
            amount: 0,
            catId:'',
            brandId:''
          }
        ]
      })
    }
    const getCatId = (e,index) => {
      console.log(e.target.value, "CatId")
      let tempCatId=e.target.value
      this.setState({tempCatId: e.target.value})
      if (this.state.tempBrandId) {
        dispatch({
          type: 'input/getAllProductsByCatAndBrand',
          payload: {
            catId: tempCatId,
            brandId: this.state.tempBrandId
          }
        }).then(res=>{
          this.state.reqProductWithAmountList[index].productList=res.object
          this.state.reqProductWithAmountList[index].catId=tempCatId
          this.setState(this.state)
        })
      } else {
        dispatch({
          type: 'input/getAllProductsByCatAndBrand',
          payload: {
            catId: tempCatId,
            brandId: null
          }
        }).then(res=>{
          this.state.reqProductWithAmountList[index].productList=res.object
          this.state.reqProductWithAmountList[index].catId=tempCatId
          this.setState(this.state)
        })
      }

    }
    const getBrandId = (e,index) => {
      console.log(e.target.value, "BrandId")
      let tempBrandId=e.target.value
      this.setState({tempBrandId: e.target.value})
      if (this.state.tempCatId) {
        dispatch({
          type: 'input/getAllProductsByCatAndBrand',
          payload: {
            catId: this.state.tempCatId,
            brandId: tempBrandId
          }
        }).then(res=>{
          this.state.reqProductWithAmountList[index].productList=res.object
          this.state.reqProductWithAmountList[index].brandId=tempBrandId
          this.setState(this.state)
        })
      } else {
        dispatch({
          type: 'input/getAllProductsByCatAndBrand',
          payload: {
            brandId: tempBrandId,
            catId: null
          }
        }).then(res=>{
          this.state.reqProductWithAmountList[index].productList=res.object
          this.state.reqProductWithAmountList[index].brandId=tempBrandId
          this.setState(this.state)
        })
      }
    }

    const inputSaveOrEdit = (e, v) => {
      console.log(v, "vvvv=======")
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
        reqProductWithAmountList:filtered
      }
      if (this.state.currentInput){
        s={...s,id:this.state.currentInput.id}
      }
      console.log(filtered, "=======filtered======")
      dispatch({
        type:'input/saveIncomeOrOutput',
        payload:s
      }).then(res=>{
        dispatch({
          type:'input/getAllInputsByPageable',
          payload:{
            page:0,
            size:10,
            confirmed:this.state.tempConfirm,
            isIncome:true
          }
        })
        toggleInputSaveOrEditModal()
      })
    }

    const getProductId = (e,index) => {
      console.log(e.target.value, "PRODUCT===ID")
      let tempId=e.target.value
      dispatch({
        type: 'input/getProductSizeListByProduct',
        payload: {
          path:tempId
        }
      }).then(res=>{
        this.state.reqProductWithAmountList[index].sizeList=res.object.productSizeList
        this.state.reqProductWithAmountList[index].productId=tempId
        this.setState(this.state)
      })
    }
    const getSize = (e,index) => {
      console.log(e.target.value, "SIZE====ID")
      this.state.reqProductWithAmountList[index].sizeId= e.target.value
      this.setState(this.state)
    }
    const getAmount=(e,index)=>{
      console.log(e.target.value, "===============AMOUNT===========")
      this.state.reqProductWithAmountList[index].amount= e.target.value
      this.setState(this.state)
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
      let temp = this.state.reqProductWithAmountList
      temp.push(s)
      this.setState({reqProductWithAmountList: temp})
    }
    const removeItem = (ind) => {
      let temp=this.state.reqProductWithAmountList
      delete temp[ind]
      this.setState({reqProductWithAmountList:temp})
    }

    const showInputProductWithAmountList=(productList)=>{
      this.setState({productWithAmountList:productList,showInputProductWithAmountModal:!this.state.showInputProductWithAmountModal})
    }
    const closeShowProductModal=()=>{
      this.setState({productWithAmountList:[],showInputProductWithAmountModal:!this.state.showInputProductWithAmountModal})
    }

    const removeInput=(id)=>{
      dispatch({
        type:'input/removeInputOrOutput',
        payload:{
          id
        }
      }).then(res=>{
        dispatch({
          type:'input/getAllInputsByPageable',
          payload:{
            page:0,
            size:10,
            confirmed:this.state.tempConfirm,
            isIncome:true
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
      this.setState({
        showInputSaveOrEditModal: !this.state.showInputSaveOrEditModal,
        currentInput: item,
        reqProductWithAmountList:tempArray
      })
    }

    const confirmInput=(id)=>{
      dispatch({
        type:'input/confirmInput',
        payload:{
          path:id
        }
      }).then(res=>{
        dispatch({
          type:'input/getAllInputsByPageable',
          payload:{
            page:0,
            size:10,
            confirmed:this.state.tempConfirm,
            isIncome:true
          }
        })
      })
    }
    const getByConfirm=(e)=>{
      let val=e.target.value
      dispatch({
        type:'input/getAllInputsByPageable',
        payload:{
          page:0,
          size:10,
          confirmed:val,
          isIncome:true
        }
      })
      this.setState({tempConfirm:val})
    }

    return (
      <div>
        <Row className="p-0 m-0">
          <Col md={{size: 2, offset: 5}}>
            <h3>Kirimlar</h3>
          </Col>
        </Row>
        <Row>
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleInputSaveOrEditModal}>+</Button>
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
                <th>Qaerdan</th>
                <th>Maxsulotlar</th>
                <th>Holati</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {allInputsByPageable?allInputsByPageable.map((item,index)=>
              <tr>
                <td>{index+1}</td>
                <td>{item.createdAt.substr(0,10)}</td>
                <td>{item.outputer!=null?item.outputer.name:''}</td>
                <td><Button type="button" onClick={()=>showInputProductWithAmountList(item.resProductWithAmountList)}>...</Button></td>
                <td><input type="checkbox" checked={item.confirmed}/></td>
                <td>{item.outputer!==null?item.confirmed?'':<Button type="button" color="success" onClick={()=>confirmInput(item.id)}>Tasdiqlash</Button>:<div>
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
              totalItemsCount={allInputsTotalElement}
              pageRangeDisplayed={5}
              onChange={this.handlePageChange.bind(this)}
              itemClass="page-item"
              linkClass="page-link"
            />
          </Col>
        </Row>

        <Modal size="lg" style={{maxWidth: '1600px', width: '80%'}} isOpen={this.state.showInputSaveOrEditModal}
               toggle={toggleInputSaveOrEditModal}>
          <ModalHeader>Kirim qilish</ModalHeader>
          <AvForm onValidSubmit={inputSaveOrEdit}>
            <ModalBody>
              {this.state.reqProductWithAmountList ? this.state.reqProductWithAmountList.map((item, index) =>
                <Row>
                  <Col>
                    <AvField type="select" className="mt-2" placeholder="Kategoriya tanlang" id={`catId${index}`} name={`catId/${index}`}
                             onChange={(e)=>getCatId(e,index)} defaultValue={item.catId}>
                      <option>Kategoriyani tanlang</option>
                      {allCategories ? allCategories.map(item =>
                        <option value={item.id}>{item.nameUz}</option>
                      ) : ''}
                    </AvField>
                  </Col>
                  <Col>
                    <AvField type="select" className="mt-2" placeholder="Brand tanlang" id={`brandId${index}`} name={`brandId/${index}`}
                             onChange={(e)=>getBrandId(e,index)} defaultValue={item.brandId}>
                      <option>Brand tanlang</option>
                      {allBrands ? allBrands.map(item =>
                        <option value={item.id}>{item.nameUz}</option>
                      ) : ''}
                    </AvField>
                  </Col>
                  <Col>
                    <AvField type="select" className="mt-2" placeholder="Maxsulot tanlang" id={`productId${index}`} name={`productId/${index}`}
                             onChange={(e)=>getProductId(e,index)} defaultValue={item.productId}>
                      <option>Mahsulot tanlang</option>
                      {item.productList ? item.productList.map(item =>
                        <option value={item.id}>{item.nameUz}</option>
                      ) : ''}
                    </AvField>
                  </Col>
                  <Col>
                    <AvField type="select" className="mt-2" placeholder="Razmer tanlang" id={`productSizeId${index}`} name={`productSizeId/${index}`}
                             onChange={(e)=>getSize(e,index)} defaultValue={item.sizeId}>
                      <option>Razmer tanlang</option>
                      {item.sizeList ? item.sizeList.map(item =>
                        <option value={item.id}>{item.name}</option>
                      ) : ''}
                    </AvField>
                  </Col>
                  <Col>
                    <AvField required={true} defaultValue={item.amount} type="number" className="mt-2" placeholder="Soni" id={`amount${index}`} name={`amount/${index}`} onChange={(e)=>getAmount(e,index)}/>
                  </Col>
                  <Col>
                    <Button className="ml-5" color="danger" style={{borderRadius: "20px"}}
                            onClick={() => removeItem(index)}>-</Button>
                  </Col>
                </Row>
              ) : ''}
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
              <Button color="danger" onClick={toggleInputSaveOrEditModal}>Bekor qilish</Button>
              <Button className="ml-3" color="success" type="submit">Saqlash</Button>
            </ModalFooter>
          </AvForm>
        </Modal>

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
