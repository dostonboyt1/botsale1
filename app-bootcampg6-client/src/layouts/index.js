import React, {Component} from 'react';
import {ToastContainer} from "react-toastify";
import {connect} from "react-redux";
import router from "umi/router";
import {Link} from "react-router-dom";

@connect(({app}) => ({app}))
class BasicLayout extends Component {
  componentDidMount() {
    const {dispatch} = this.props
    dispatch({
      type: 'app/userme'
    }).then(res => {
      if (res.success) {
        {
          const {props} = this;
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
            }
          }
        }
      }
    })
  }

  render() {
    const url = window.location.pathname;
    const {currentUser} = this.props.app
    console.log(currentUser, "USER=============")
    console.log(url, "URL")
    const logout = () => {
      localStorage.removeItem("botsale-token")
      router.push("/")
      this.props.dispatch({
        type: 'app/updateState',
        payload: {
          currentUser: ''
        }
      })
    }
    return (
      <div>
        <ToastContainer/>
        {currentUser ?
          <div className="row w-100">
            <div className="col-md-2 bg-secondary" style={{height: "740px"}}>
              {currentUser.roles.filter(item=>item.name==='ROLE_DIRECTOR').length?
                <div className="mt-2"  style={{textAlign: "center"}}>
                  <Link to="/dashboard">
                    <h3>Hisobotlar</h3>
                  </Link>
                </div>:''
              }
              {currentUser.roles.filter(item=>item.name==='ROLE_ADMIN').length?
              <div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/category">
                    <h3>Kategoriyalar</h3>
                  </Link>
                </div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/brand">
                    <h3>Brand</h3>
                  </Link>
                </div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/productSize">
                    <h3>O'lchamlar</h3>
                  </Link>
                </div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/product">
                    <h3>Maxsulotlar</h3>
                  </Link>
                </div>

                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/warehouse">
                    <h3>Ombor</h3>
                  </Link>
                </div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/employee">
                    <h3>Xodimlar</h3>
                  </Link>
                </div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/region">
                    <h3>Region</h3>
                  </Link>
                </div>
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/district">
                    <h3>District</h3>
                  </Link>
                </div>
              </div>:''
              }
              {currentUser.roles.filter(item=>item.name==='ROLE_WAREHOUSEKEEPER').length?
                <div>
                  <div className="mt-2" style={{textAlign: "center"}}>
                    <Link to="/input">
                      <h3>Kirim</h3>
                    </Link>
                  </div>
                  <div className="mt-2" style={{textAlign: "center"}}>
                    <Link to="/output">
                      <h3>Chiqim</h3>
                    </Link>
                  </div>
                  <div className="mt-2" style={{textAlign: "center"}}>
                    <Link to="/warehouseOrder">
                      <h3>Buyurtma chiqimi</h3>
                    </Link>
                  </div>
                </div>:''
              }
              {currentUser.roles.filter(item=>item.name==='ROLE_OPERATOR').length?
                <div className="mt-2" style={{textAlign: "center"}}>
                  <Link to="/order">
                    <h3>Buyurtmalar</h3>
                  </Link>
                </div>:''
              }

              <div className="mt-2" style={{textAlign: "center"}} onClick={logout}>
                <Link to="/">
                  <h3>Chiqish</h3>
                </Link>
              </div>
            </div>

            <div className="col-md-10 pt-5">
              {this.props.children}
            </div>

          </div>
          : <div>
            {this.props.children}
          </div>
        }
      </div>
    );
  }
}

export default BasicLayout;
