import React, {Component} from 'react';
import {connect} from "react-redux";
import {
  Breadcrumb,
  BreadcrumbItem,
  Col,
  Row,
  Button,
  Table,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter
} from "reactstrap";
import {AvField, AvForm} from "availity-reactstrap-validation";

@connect(({region}) => ({region}))
class Index extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isAddModal: false,
      currentRegionSize: '',
      tempId: '',
      showDeletingModal: false,
      // activePage: 1
    }
  }

  componentDidMount() {
    this.itemsInState()
  }

  itemsInState = () => {
    const {dispatch} = this.props;
    console.log(this.props, "this is region stat");
    dispatch({
      type: 'region/getAllRegion',
    })
  };

  render() {
    const {dispatch, region} = this.props;
    const {regionPage} = region;

    const editRegion = (item) => {
      this.setState({
        isAddModal: !this.state.isAddModal, currentRegionSize: item
      })
    };
    const deleteRegion = (regionId) => {
      this.setState({tempId: regionId, showDeletingModal: !this.state.showDeletingModal})
    };
    const addRegionControl = () => {
      this.setState({
        isAddModal: !this.state.isAddModal, currentRegionSize: ''
      })
    };
    const regionSaveOrEdit = (e, v) => {
      if (this.state.currentRegionSize) {
        v = {...v, id: this.state.currentRegionSize.id}
      }
      dispatch({
        type: 'region/regionSaveOrEdit',
        payload: v
      }).then(res => {
        this.itemsInState();
        this.setState({isAddModal: !this.state.isAddModal, currentRegionSize: ''})
      })
    };
    const toggleDeleteModal = () => {
      this.setState({tempId: '', showDeletingModal: !this.state.showDeletingModal})
    };
    const RowIsDel = () => {
      if (this.state.tempId) {
        dispatch({
          type: 'region/deleteRegion',
          payload: {
            id: this.state.tempId
          }
        }).then(res => {
          this.itemsInState();
          this.setState({showDeletingModal: !this.state.showDeletingModal})
        })
      }
    }

    return (
      <div>
        <Breadcrumb>
          <BreadcrumbItem><a href="#">Home</a></BreadcrumbItem>
          <BreadcrumbItem active>Region page</BreadcrumbItem>
        </Breadcrumb>
        <Row>
          <Col className="text-center">
            <h1>Regions Page</h1>
          </Col>
        </Row>
        <Row>
          <Col className="mt-2" md={{size: 10, offset: 1}}>
            <Button onClick={addRegionControl} color="primary" style={{borderRadius: "20px"}}>Add new Region</Button>
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
                <th>Opiration</th>
              </tr>
              </thead>
              <tbody>
              {regionPage ?
                regionPage.map((item, index) =>
                  <tr>
                    <td>{index + 1}</td>
                    <td>{item.nameUz}</td>
                    <td>{item.nameRu}</td>
                    <td>
                      <Button outline onClick={() => editRegion(item)} color="warning">Edit</Button>
                      <Button outline onClick={() => deleteRegion(item.id)} className="ml-2"
                              color="danger">Delete</Button>
                    </td>
                  </tr>
                ) :
                <tr>
                  <td>Hozrda malumot mavjud emas</td>
                  <td>null</td>
                  <td>null</td>
                  <td>null</td>
                </tr>}
              </tbody>
            </Table>
          </Col>
        </Row>
        <Modal isOpen={this.state.isAddModal} toggle={addRegionControl}>
          <ModalHeader
            toggle={addRegionControl}>
            {this.state.currentRegionSize ? "Editing 📝" : "Add new Region 🖊"}
          </ModalHeader>
          <AvForm onValidSubmit={regionSaveOrEdit}>
            <ModalBody>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentRegionSize ? this.state.currentRegionSize.nameUz : ''}
                       className="mt-2" placeholder="Enter Region nameUz" name="nameUz"/>
              <AvField required={true} type="text"
                       defaultValue={this.state.currentRegionSize ? this.state.currentRegionSize.nameRu : ''}
                       className="mt-2" placeholder="Enter Region nameRu" name="nameRu"/>
            </ModalBody>
            <ModalFooter>
              <Button outline onClick={addRegionControl}>Cancel</Button>
              <Button outline color="primary">{this.state.currentRegionSize ? "Etid" : "Save"}</Button>
            </ModalFooter>
          </AvForm>
        </Modal>
        <Modal isOpen={this.state.showDeletingModal} toggle={toggleDeleteModal}>
          <ModalHeader toggle={this.toggleDeleteModal}>Kategoriyani o'chirishni istaysizmi?</ModalHeader>
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
