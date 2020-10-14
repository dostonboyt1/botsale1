import React from "react";
import {Nav, NavbarText, NavItem, NavLink} from 'reactstrap';
import {connect} from "react-redux";

@connect(({app}) => ({app}))
class MainNav extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      admin: false,
      operator: false,
      director: false,
      warehouseKeeper: false
    };
  }

  componentDidMount() {
    const {props} = this;
    if (props.app.currentUser) {
      const {roles} = props.app.currentUser;
      if (roles) {
        roles.map(role => {
            if (role.name === 'ROLE_DIRECTOR') {
              this.setState({director: true})
            } else if (role.name === 'ROLE_ADMIN') {
              this.setState({admin: true})
            } else if (role.name === 'ROLE_WAREHOUSEKEEPER') {
              this.setState({warehouseKeeper: true})
            } else if (role.name === 'ROLE_OPERATOR') {
              this.setState({operator: true})
            }
          }
        );
      }
    }
  }

  render() {
    const toggle = () => this.setState({isOpen: !this.state.isOpen});
    // const sale = () => this.setState({isSale: !this.state.isSale});
    // const sale2 = () => {
    //   this.setState({isSale2: !this.state.isSale2})
    //   this.props.dispatch({
    //     type: 'app/updateState',
    //     payload: {
    //       currentUser2: ''
    //     }
    //   })
    // };
    const url = window.location.pathname;
    const {warehouseKeeper, director, admin, operator} = this.state;
    // distribution(url, this.state);
    // const clear = () => {
    //   window.localStorage.clear();
    // };
    return (
      <React.Fragment>
        <div className="erp-leftnav bg-white text-center">
          <div className="mt-4">
            {/*<a href="/" onClick={clear}>*/}
            {/*  <img src={Logo} className='img-fluid' alt=""/>*/}
            {/*</a>*/}
          </div>

          {director ?
            <Nav navbar className='mt-3'>
              <NavItem className='pooltip'>
                <NavLink className={url === "/dashboard" ? 'active' : ''} href="/dashboard">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="none" d="M0 0h24v24H0z"/>
                    <path
                      d="M21 20a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9.49a1 1 0 0 1 .386-.79l8-6.222a1 1 0 0 1 1.228 0l8 6.222a1 1 0 0 1 .386.79V20zm-2-1V9.978l-7-5.444-7 5.444V19h14z"
                      fill="rgba(208,214,220,1)"/>
                  </svg>
                </NavLink>
                <span className="pooltiptext">Hisobotlar</span>
              </NavItem>
            </Nav>
            : ''}
          {operator ?
            <Nav navbar className='mt-3'>
              <NavItem className='pooltip'>
                <NavLink className={url === "/orders" ? 'active' : ''} href="/orders">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="none" d="M0 0h24v24H0z"/>
                    <path
                      d="M19 22H5a3 3 0 0 1-3-3V3a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v12h4v4a3 3 0 0 1-3 3zm-1-5v2a1 1 0 0 0 2 0v-2h-2zm-2 3V4H4v15a1 1 0 0 0 1 1h11zM6 7h8v2H6V7zm0 4h8v2H6v-2zm0 4h5v2H6v-2z"
                      fill="rgba(208,214,220,1)"/>
                  </svg>
                </NavLink>
                <span className="pooltiptext">Buyurtmalar</span>
              </NavItem>
            </Nav>
            : ''}
          {warehouseKeeper ?
            <Nav navbar className='mt-3'>
              <NavItem className='pooltip'>
                <NavLink className={url === "/input" ? 'active' : ''}
                         href="/input">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="none" d="M0 0h24v24H0z"/>
                    <path
                      d="M11 19V9H4v10h7zm0-12V4a1 1 0 0 1 1-1h9a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V8a1 1 0 0 1 1-1h8zm2-2v14h7V5h-7zM5 16h5v2H5v-2zm9 0h5v2h-5v-2zm0-3h5v2h-5v-2zm0-3h5v2h-5v-2zm-9 3h5v2H5v-2z"
                      fill="rgba(208,214,220,1)"/>
                  </svg>
                </NavLink>
                <span className="pooltiptext">Kirim</span>
              </NavItem>
              <NavItem className='pooltip'>
                <NavLink className={url === "/output" ? 'active' : ''}
                         href="/output">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="none" d="M0 0h24v24H0z"/>
                    <path
                      d="M11 19V9H4v10h7zm0-12V4a1 1 0 0 1 1-1h9a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V8a1 1 0 0 1 1-1h8zm2-2v14h7V5h-7zM5 16h5v2H5v-2zm9 0h5v2h-5v-2zm0-3h5v2h-5v-2zm0-3h5v2h-5v-2zm-9 3h5v2H5v-2z"
                      fill="rgba(208,214,220,1)"/>
                  </svg>
                </NavLink>
                <span className="pooltiptext">Chiqim</span>
              </NavItem>
              <NavItem className='pooltip'>
                <NavLink className={url === "/warehouseOrder" ? 'active' : ''}
                         href="/warehouseOrder">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="none" d="M0 0h24v24H0z"/>
                    <path
                      d="M11 19V9H4v10h7zm0-12V4a1 1 0 0 1 1-1h9a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V8a1 1 0 0 1 1-1h8zm2-2v14h7V5h-7zM5 16h5v2H5v-2zm9 0h5v2h-5v-2zm0-3h5v2h-5v-2zm0-3h5v2h-5v-2zm-9 3h5v2H5v-2z"
                      fill="rgba(208,214,220,1)"/>
                  </svg>
                </NavLink>
                <span className="pooltiptext">Buyurtmalar</span>
              </NavItem>
            </Nav>
            : ''}
          {admin ?
            <Nav navbar className='mt-3'>
              <NavItem>
                <NavLink href="/employee">
                  <span>Xodimlar</span>
                </NavLink>
              </NavItem>
              <NavItem>
                <NavLink href="/category">
                  <span>Kategoriya</span>
                </NavLink>
              </NavItem>
              <NavItem className='pooltip'>
                <NavLink className={url === "/brand" ? 'active' : ''} href="/brand">
                  <span className="pooltiptext">Brand</span>
                </NavLink>
              </NavItem>
              <NavItem className='pooltip'>
                <NavLink className={url === "/productSize" ? 'active' : ''} href="/productSize">
                  <span className="pooltiptext">Product size</span>
                </NavLink>
              </NavItem>
              <NavItem className='pooltip'>
                <NavLink className={url === "/product" ? 'active' : ''} href="/product">
                  <span className="pooltiptext">Maxsulotlar</span>
                </NavLink>
              </NavItem>
              <NavItem className='pooltip'>
                <NavLink className={url === "/warehouse" ? 'active' : ''} href="/warehouse">
                  <span className="pooltiptext">Omborlar</span>
                </NavLink>
              </NavItem>
            </Nav>
            : ''}
          <NavbarText>
            <div className="admin-prof">
              <img src="/assets/images/Oval.png" alt=""/>
            </div>
          </NavbarText>
        </div>
        {/*{this.state.isSale ?*/}
        {/*  <SalerPage params={{toggle: sale, isOpen: this.state.isSale}}/>*/}

        {/*  : ''}*/}
        {/*{this.state.isSale2 ?*/}
        {/*  <SalerPage2 params={{toggle: sale2, isOpen: this.state.isSale2}}/>*/}
        {/*  : ''}*/}
      </React.Fragment>

    );
  }
}

export default MainNav;
