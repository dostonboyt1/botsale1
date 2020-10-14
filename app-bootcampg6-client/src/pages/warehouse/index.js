import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {connect} from "react-redux";
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import option from "eslint-plugin-jsx-a11y/lib/util/implicitRoles/option";

@connect(({warehouse}) => ({warehouse}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state={
      shoWarehouseSaveOrEditModal:false,
      currentWarehouse:'',
    }
  }
  componentDidMount() {
    const {dispatch}=this.props
    dispatch({
      type:'warehouse/getAllWarehouse'
    })
    dispatch({
      type:'warehouse/getAllRegion'
    })
    dispatch({
      type:'warehouse/getAllUsers'
    })
  }

  render() {
    const {warehouse,dispatch}=this.props
    const {allWarehouse,allDistrict,allRegion,allUser}=warehouse
    const toggleWarehouseSaveOrEditModal = () => {
      this.setState({shoWarehouseSaveOrEditModal: !this.state.shoWarehouseSaveOrEditModal, currentWarehouse: ''})
    }
    const editWarehouse = (item) => {
      dispatch({
        type:'warehouse/getAllDistrict',
        payload:{
          id:item.regionId
        }
      }).then(res=>{
        this.setState({shoWarehouseSaveOrEditModal: !this.state.shoWarehouseSaveOrEditModal, currentWarehouse: item})
      })
    }
    const deleteWarehouse = (id) => {
      dispatch({
        type:'warehouse/deleteWarehouse',
        payload: {
          id
        }
      }).then(res=>{
        dispatch({
          type:'warehouse/getAllWarehouse'
        })
      })
    }
    const warehouseSaveOrEdit = (e, v) => {
      console.log(v, "VALUES")
      if (this.state.currentWarehouse){
        v={...v,id:this.state.currentWarehouse.id}
      }
      dispatch({
        type:'warehouse/saveOrEdit',
        payload:v
      }).then(res=>{
        dispatch({
          type:'warehouse/getAllWarehouse'
        })
        toggleWarehouseSaveOrEditModal();
      })
    }
    const getDistrictByRegion=(e)=>{
      dispatch({
        type:'warehouse/getAllDistrict',
        payload:{
          id:e.target.value
        }
      })
    }
    return (
      <div>
        <Row>
          <Col>
            <h3 className="text-center">Omborlar</h3>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleWarehouseSaveOrEditModal}>+</Button>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Nomi</th>
                <th>Manzil</th>
                <th>Xodim</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {allWarehouse ?
                allWarehouse.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.name}</td>
                    <td>{item.address}</td>
                    <td>{item.resUser?item.resUser.firstName+" "+item.resUser.lastName:''}</td>
                    <td>
                      <Button color="warning" onClick={() => editWarehouse(item)}>Edit</Button>

                      <Button className="ml-3" color="danger" onClick={() => deleteWarehouse(item.id)}>Delete</Button>

                    </td>
                  </tr>
                )
                : ''}
              </tbody>
            </Table>

            <Modal isOpen={this.state.shoWarehouseSaveOrEditModal} toggle={toggleWarehouseSaveOrEditModal}>
              <ModalHeader>{this.state.currentWarehouse ? "Omborni tahrirlash" : "Yangi ombor qo'shish"}</ModalHeader>
              <AvForm onValidSubmit={warehouseSaveOrEdit}>
                <ModalBody>
                  <AvField required={true} type="text"
                           defaultValue={this.state.currentWarehouse ? this.state.currentWarehouse.name : ''}
                           className="mt-2" placeholder="Nomi" name="name"/>
                  <AvField required={true} type="select"
                           defaultValue={this.state.currentWarehouse ? this.state.currentWarehouse.regionId : ''}
                           className="mt-2" name="regionId" onChange={getDistrictByRegion}>
                    <option>Viloyat tanlang:</option>
                    {allRegion?allRegion.map(item=>
                      <option value={item.id}>{item.nameUz}</option>
                    ):''}
                  </AvField>
                  <AvField required={true} type="select"
                           defaultValue={this.state.currentWarehouse ? this.state.currentWarehouse.districtId : ''}
                           className="mt-2" name="districtId">
                    <option>Tumanni tanlang:</option>
                    {allDistrict?allDistrict.map(item=>
                      <option value={item.id}>{item.nameUz}</option>
                    ):''}
                  </AvField>
                  <AvField required={true} type="text"
                           defaultValue={this.state.currentWarehouse ? this.state.currentWarehouse.address : ''}
                           className="mt-2" placeholder="Manzil" name="address"/>

                  <AvField required={true} type="select"
                           defaultValue={this.state.currentWarehouse.resUser ? this.state.currentWarehouse.resUser.id : ''}
                           className="mt-2" name="userId">
                    <option>Xodim tanlang:</option>
                    {allUser?allUser.map(item=>
                      <option value={item.id}>{item.firstName+" "+item.lastName}</option>
                    ):''}
                  </AvField>
                  <AvField required={true} type="select"
                           defaultValue={this.state.currentWarehouse ? this.state.currentWarehouse.warehouseStatus : ''}
                           className="mt-2" name="warehouseStatus">
                    <option>Ombor turini tanlang:</option>
                    <option value="MAIN">Asosiy</option>
                    <option value="SECONDARY">Qo'shimcha</option>
                  </AvField>
                </ModalBody>
                <ModalFooter>
                  <Button color="danger" onClick={toggleWarehouseSaveOrEditModal}>Bekor qilish</Button>

                  <Button className="ml-3" color="success" type="submit">Saqlash</Button>
                </ModalFooter>
              </AvForm>
            </Modal>
          </Col>
        </Row>

      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
