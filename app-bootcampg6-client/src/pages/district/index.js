import React, {Component} from 'react';
import {connect} from "react-redux";
import {
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Col,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
  Row,
  Table
} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";
import Pagination from "react-js-pagination";

@connect(({district, app}) => ({district, app}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isAddModal: false,
      currentElementSize: '',
      tempId: '',
      showDeletingModal: false,
      activePage: 1
    }
  }

  componentDidMount() {
    this.itemsInState()
  }

  itemsInState = () => {
    const {dispatch} = this.props;
    console.log(this.props, "this is region stat");
    dispatch({
      type: 'district/districtGetByPagable',
      payload: {
        page: 0,
        size: 5
      }
    })
    dispatch({
      type: 'app/getAllRegion'
    })
  };

  handlePageChange(pageNumber) {
    const {dispatch} = this.props
    console.log(`active page is ${pageNumber}`);
    this.setState({activePage: pageNumber});
    dispatch({
      type: 'district/districtGetByPagable',
      payload: {
        page: pageNumber - 1,
        size: 5
      }
    })
  }


  render() {
    const {dispatch, district, app} = this.props;
    const {districtPage, districtTotalElements} = district;
    const {allRegions} = app;
    const addNewElement = () => {
      this.setState({
        isAddModal: !this.state.isAddModal, currentElementSize: ''
      })
    };
    const saveOrEdit = (e, v) => {
      if (this.state.currentElementSize) {
        v = {...v, id: this.state.currentElementSize.id}
      }
      dispatch({
        type: 'district/districtSaveOrEdit',
        payload: v
      }).then(res => {
        console.log(res, "add Ress console++++++++++++++++")
        this.itemsInState();
        this.setState({isAddModal: !this.state.isAddModal, currentElementSize: ''})
      })
    };
    const addNewRoqItems = () => {
      this.setState({
        isAddModal: !this.state.isAddModal, currentElementSize: ''
      })
    };
    const toggleDeleteModal = () => {
      this.setState({
        showDeletingModal: !this.state.showDeletingModal, tempId: ''
      })
    };
    const RowIsDel = () => {
      if (this.state.tempId) {
        dispatch({
          type: 'district/deletedistrict',
          payload: {
            id: this.state.tempId
          }
        }).then(res => {
          this.itemsInState()
          this.setState({showDeletingModal: !this.state.showDeletingModal})
        })
      }
    };
    const deleteDistrict = (disrictId) => {
      this.setState({
        tempId: disrictId, showDeletingModal: !this.state.showDeletingModal
      })
    }
    const editDistrict = (item) => {
      this.setState({
        isAddModal: !this.state.isAddModal, currentElementSize: item
      })
    };
    return (
      <div>
        <Breadcrumb>
          <BreadcrumbItem><a href="#">Home</a></BreadcrumbItem>
          <BreadcrumbItem active>District page</BreadcrumbItem>
        </Breadcrumb>
        <Row>
          <Col className="text-center">
            <h1>District Page</h1>
          </Col>
        </Row>
        <Row>
          <Col className="mt-2" md={{size: 10, offset: 1}}>
            <Button onClick={addNewElement} color="primary" style={{borderRadius: "20px"}}>Add new District</Button>
          </Col>
        </Row>
        <Row className="mt-2">
          <Col md={{size: 10, offset: 1}}>
            <Table striped hover>
              <thead>
              <tr>
                <th>#</th>
                <th>NameUz</th>
                <th>NameRu</th>
                <th>Region</th>
                <th>Opiration</th>
              </tr>
              </thead>
              <tbody>
              {console.log(districtPage, "district ++++++++++++++++++++++++++++++++++++++++")}
              {districtPage ?
                districtPage.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.nameUz}</td>
                    <td>{item.nameRu}</td>
                    <td>{item.region.nameUz}</td>
                    <td>
                      <Button outline onClick={() => editDistrict(item)} color="warning">Edit</Button>
                      <Button outline onClick={() => deleteDistrict(item.id)} className="ml-2"
                              color="danger">Delete</Button>
                    </td>
                  </tr>
                ) :
                <tr>
                  <td>Hozrda malumot mavjud emas</td>
                  <td>null</td>
                  <td>null</td>
                  <td>null</td>
                  <td>null</td>
                </tr>}
              </tbody>
            </Table>
          </Col>
        </Row>
        <Row>
          <Col md={{size:10 , offset:1}}>
            <Pagination
              activePage={this.state.activePage}
              itemsCountPerPage={5}
              totalItemsCount={districtTotalElements}
              pageRangeDisplayed={5}
              onChange={this.handlePageChange.bind(this)} itemClass="page-item"
              linkClass="page-link"
            />
          </Col>
        </Row>
        <Modal isOpen={this.state.isAddModal} toggle={addNewRoqItems}>
          <ModalHeader
            toggle={addNewRoqItems}>{this.state.currentElementSize ? "Editing 📝" : "Add new Region 🖊"}</ModalHeader>
          <AvForm onValidSubmit={saveOrEdit}>
            <ModalBody>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentElementSize ? this.state.currentElementSize.nameUz : ''}
                       className="mt-2" placeholder="Enter Region nameUz" name="nameUz"/>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentElementSize ? this.state.currentElementSize.nameRu : ''}
                       className="mt-2" placeholder="Enter Region nameRu" name="nameRu"/>
              <AvField type="select"
                       defaultValue={this.state.currentElementSize ? this.state.currentElementSize.region.id : ''}
                       className="mt-2" placeholder="Enter Region" name="regionId">
                <option>Enter Region for district</option>
                {allRegions ? allRegions.map(item =>
                  <option value={item.id}>{item.nameUz}</option>
                ) : ''}
              </AvField>
            </ModalBody>
            <ModalFooter>
              <Button outline onClick={addNewRoqItems}>Cancel</Button>
              <Button outline color="primary">{this.state.currentElementSize ? "Etid" : "Save"}</Button>
            </ModalFooter>
          </AvForm>
        </Modal>
        <Modal isOpen={this.state.showDeletingModal} toggle={toggleDeleteModal}>
          <ModalHeader toggle={this.toggleDeleteModal}>Are you want to delete ?_?</ModalHeader>
          <ModalBody></ModalBody>
          <ModalFooter>
            <Button color="danger" onClick={toggleDeleteModal}>Bekor qilish</Button>
            <Button className="ml-3" color="info" type="button" onClick={RowIsDel}>O'chirish</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

export default Index;
