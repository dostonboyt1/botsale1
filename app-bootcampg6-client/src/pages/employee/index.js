import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {connect} from "react-redux";
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row, Table} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import Pagination from "react-js-pagination";


@connect(({employee}) => ({employee}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showEmployeeSaveOrEditModal: false,
      currentEmployee: '',
      activePage: 1

    }
  }

  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'employee/getAllEmployeeByPageable',
      payload: {
        page: 0,
        size: 10
      }
    })
  }
  handlePageChange(pageNumber) {
    const {dispatch} = this.props
    console.log(`active page is ${pageNumber}`);
    this.setState({activePage: pageNumber});
    dispatch({
      type: 'employee/getAllEmployeeByPageable',
      payload: {
        page: pageNumber-1,
        size: 10
      }
    })

  }

  render() {
    const {dispatch, employee} = this.props
    const {totalEmployeesAmount, allEmployeeByPageable} = employee

    const toggleEmployeeSaveOrEditModal = () => {
      this.setState({showEmployeeSaveOrEditModal: !this.state.showEmployeeSaveOrEditModal, currentEmployee: ''})
    }
    const editEmployee = (item) => {
      this.setState({showEmployeeSaveOrEditModal: !this.state.showEmployeeSaveOrEditModal, currentEmployee: item})
    }
    const changeEnabled = (id,status) => {
      dispatch({
        type:'employee/changeEnable',
        payload:{
          id,
          status
        }
      }).then(res=>{
        dispatch({
          type: 'employee/getAllEmployeeByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
      })
    }
    const deleteEmployee = (id) => {
      dispatch({
        type:'employee/deleteEmployee',
        payload:{
          id
        }
      }).then(res=>{
        dispatch({
          type: 'employee/getAllEmployeeByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
      })
    }
    const employeeSaveOrEdit = (e, v) => {
      console.log(v, "VALUES")
      if (this.state.currentEmployee){
        v={...v,id:this.state.currentEmployee.id}
      }
      dispatch({
        type:'employee/saveOrEdit',
        payload:v
      }).then(res=>{
        dispatch({
          type: 'employee/getAllEmployeeByPageable',
          payload: {
            page: 0,
            size: 10
          }
        })
        toggleEmployeeSaveOrEditModal();
      })
    }
    return (
      <div>
        <Row>
          <Col>
            <h3 className="text-center">Hodimlar</h3>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Button className="ml-5" color="success" style={{borderRadius: "20px"}}
                    onClick={toggleEmployeeSaveOrEditModal}>+</Button>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col>
            <Table>
              <thead>
              <tr>
                <th>T/R</th>
                <th>Ism</th>
                <th>Familiya</th>
                <th>Login</th>
                <th>Mansabi</th>
                <th>Aktiv</th>
                <th>Amallar</th>
              </tr>
              </thead>
              <tbody>
              {allEmployeeByPageable ?
                allEmployeeByPageable.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.firstName}</td>
                    <td>{item.lastName}</td>
                    <td>{item.phoneNumber}</td>
                    <td>{item.roleNameList ? item.roleNameList.map(item => <span>{item + " "}<br/> </span>) : ''}</td>
                    <td><input type="checkbox" checked={item.enabled}/></td>
                    <td>
                      <Button color="warning" onClick={() => editEmployee(item)}>Edit</Button>

                      <Button className="ml-3" color="info"
                              onClick={() => changeEnabled(item.id,item.enabled ? false : true)}>{item.enabled ? "Bloklash" : "Aktivlashtirish"}</Button>

                      <Button className="ml-3" color="danger" onClick={() => deleteEmployee(item.id)}>Delete</Button>

                    </td>
                  </tr>
                )
                : ''}
              </tbody>
            </Table>
            <Row>
              <Col>
                <Pagination
                  activePage={this.state.activePage}
                  itemsCountPerPage={10}
                  totalItemsCount={totalEmployeesAmount}
                  pageRangeDisplayed={5}
                  onChange={this.handlePageChange.bind(this)}itemClass="page-item"
                  linkClass="page-link"
                />
              </Col>
            </Row>

            <Modal isOpen={this.state.showEmployeeSaveOrEditModal} toggle={toggleEmployeeSaveOrEditModal}>
              <ModalHeader>{this.state.currentEmployee ? "Hodimni tahrirlash" : "Yangi hodim qo'shish"}</ModalHeader>
              <AvForm onValidSubmit={employeeSaveOrEdit}>
                <ModalBody>
                  <AvField required={true} type="text"
                           defaultValue={this.state.currentEmployee ? this.state.currentEmployee.firstName : ''}
                           className="mt-2" placeholder="Ism" name="firstName"/>
                  <AvField required={true} type="text"
                           defaultValue={this.state.currentEmployee ? this.state.currentEmployee.lastName : ''}
                           className="mt-2" placeholder="Familiya" name="lastName"/>
                  <AvField required={true} type="text"
                           defaultValue={this.state.currentEmployee ? this.state.currentEmployee.phoneNumber : ''}
                           className="mt-2" placeholder="Login" name="phoneNumber"/>
                  <AvField required={true} type="text"
                           className="mt-2" placeholder="Parol" name="password"/>
                  <AvField required={true} type="select"
                           defaultValue={this.state.currentEmployee.roleNameList ? this.state.currentEmployee.roleNameList[0] : ''}
                           className="mt-2" name="roleName">
                    <option>Mansabi:</option>
                    <option value="ROLE_DIRECTOR">Direktor</option>
                    <option value="ROLE_ADMIN">Administrator</option>
                    <option value="ROLE_OPERATOR">Operator</option>
                    <option value="ROLE_WAREHOUSEKEEPER">Omborchi</option>
                  </AvField>
                </ModalBody>
                <ModalFooter>
                  <Button color="danger" onClick={toggleEmployeeSaveOrEditModal}>Bekor qilish</Button>

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
