import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Col, Row, TabContent, TabPane, Nav, NavItem, NavLink, Button, Table} from "reactstrap";
import classnames from 'classnames';
import {connect} from "react-redux";

@connect(({dashboard})=>({dashboard}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state={
      activeTab:1,

    }

  }
  componentDidMount() {
    const {dispatch}=this.props
    dispatch({
      type:'dashboard/getAllBalance'
    })
  }

  render() {
    const {dispatch,dashboard}=this.props
    const {balance}=dashboard
    const toggle = (tab) => {
      if (this.state.activeTab !== tab){
       this.setState({
         activeTab:tab
       })
      }

    }
    const excel=(id)=>{
      const url = '/api/excel/' + id;
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', "excel.xlsx"); //or any other extension
      document.body.appendChild(link);
      link.click();
    }
    return (
      <div>
        <Row>
          <Col>
            <h1>Hisobotlar Page</h1>
          </Col>
        </Row>
        <Row>
          <Col>
            <Nav tabs>
              <NavItem>
                <NavLink
                  className={classnames({active: this.state.activeTab === '1'})}
                  onClick={() =>
                    {toggle('1')}
                  }
                >
                  Usul 1
                </NavLink>
              </NavItem>
              <NavItem>
                <NavLink
                  className={classnames({active: this.state.activeTab === '2'})}
                  onClick={() =>{ toggle('2')}} >
                  Usul 2
                </NavLink>
              </NavItem>
            </Nav>
            <TabContent activeTab={this.state.activeTab}>
              <TabPane tabId="1">
                <Row>
                  <Col sm="12">
                    <h4>Mahsulot qoldig'i</h4>
                  </Col>
                </Row>
                <Row>
                  <Col>
                    <Table>
                      <thead>
                      <tr>
                        <th>T/r</th>
                        <th>Ombor nomi</th>
                        <th>Jami qoldiq</th>
                        <th>Mahsulotlar Excel</th>
                      </tr>
                      </thead>
                      <tbody>
                      {balance?balance.map((item,index)=>
                      <tr>
                        <td>{index+1}</td>
                        <td>{item.warehouseName}</td>
                        <td>{item.warehouseBalance}</td>
                        <td><Button color="info" type="button" onClick={()=>excel(item.warehouseId)}>Export to Excel</Button></td>
                      </tr>
                      ):''}
                      </tbody>
                    </Table>
                  </Col>
                </Row>
              </TabPane>
              <TabPane tabId="2">
                <Row>
                  <Col sm="6">
                  </Col>
                </Row>
              </TabPane>
            </TabContent>
          </Col>
        </Row>


      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
