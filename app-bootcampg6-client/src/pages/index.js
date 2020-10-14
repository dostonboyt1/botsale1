import React, {Component} from 'react';
import {Button, Col, Container,Row, Input, InputGroup} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import {connect} from "react-redux";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min";
import maincss from "../css/main.css";
import "../css/animate.css"
import {router} from 'umi';

@connect(({app})=>({app}))
class Index extends Component {
  render() {
    const {dispatch,app}=this.props
    const login = (e, v) => {
      console.log(v,"values==============")
      dispatch({
        type:'app/signIn',
        payload:v
      }).then(res=>{
        // if (res.status!==401){
          window.location='/'
          dispatch({
            type:'app/userme'
          }).then(res=>{
            if (res.success){
              {const {props} = this;
                if (props.app.currentUser) {
                  const {roles} = props.app.currentUser;
                  if (roles) {
                    roles.map(role => {
                        if (role.name === 'ROLE_DIRECTOR') {
                          router.push('/dashboard')
                        } else if (role.name === 'ROLE_ADMIN') {
                          router.push('/employee')
                        } else if (role.name === 'ROLE_OPERATOR') {
                          router.push('/order')
                        } else if (role.name === 'ROLE_WAREHOUSEKEEPER') {
                          router.push('/input')
                        }
                      }
                    );
                  }}
              }
            }
          })
        // }
      })

    }
    return (
      <div>
        <Container fluid className={maincss.image}>
          <Row>
            <Col md={4}  className="mt-5 wrap-login100 offset-4 d-flex flex-column p-0 justify-content-center align-items-center">
              <div className="mt-3">
                <h3 className='text-center my-3'>LOGIN</h3>
              </div>
              <div className="w-75">
                <AvForm onValidSubmit={login}>
                  <div className="form-group loginFormGroup">
                    <span>Phone</span>
                    <AvField required={true} type="text" className="mt-2" placeholder="Login" name="phoneNumber"/>
                    <span>Password</span>
                    <AvField required={true} type="password" placeholder="***..." name="password"/>
                    <div className="container-login100-form-btn">
                      <div className="wrap-login100-form-btn">
                        <div className="login100-form-bgbtn"></div>
                        <Button type="submit" className=" login100-form-btn">Kirish</Button>
                      </div>
                    </div>
                  </div>
                </AvForm>
              </div>
            </Col>
          </Row>
        </Container>
      </div>
    );
  }
}

Index.propTypes = {};

export default Index;
