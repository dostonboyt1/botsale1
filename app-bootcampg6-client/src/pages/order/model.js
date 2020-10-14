import api from 'services'

const {getAllOrdersByStatus,changeOrderStatus,getAllProductsByCatAndBrand,getProductSizeListByProduct,saveOrder} = api

export default {
  namespace:'order',
  state:{
    allNewOrders:[ ],
    newTotal:0,
    allApprovedOrders:[],
    approvedTotal:0,
    allSendOrders:[],
    sendTotal:0,
    allRecievedOrders:[],
    recievedTotal:0,
    allProductsByCatAndBrand:[],
    sizeListByProduct:[],
    selectedProduct:[]
  },
  subscriptions:{},
  effects:{
    * getAllOrdersByStatus({payload},{call,select,put}){
      const res=yield call(getAllOrdersByStatus,payload)
      console.log(res,"BYSTATUS========")
      console.log(payload.orderStatus,"STATUS==")
      if (payload.orderStatus==='NEW'){
        yield put({
          type:'updateState',
          payload:{
            allNewOrders:res.object,
            newTotal:res.totalElements
          }
        })
      }
      if (payload.orderStatus==='APPROVED'){
        yield put({
          type:'updateState',
          payload:{
            allApprovedOrders:res.object,
            approvedTotal:res.totalElements
          }
        })
      }
      if (payload.orderStatus==='SEND'){
        yield put({
          type:'updateState',
          payload:{
            allSendOrders:res.object,
            sendTotal:res.totalElements
          }
        })
      }
      if (payload.orderStatus==='RECIEVED'){
        yield put({
          type:'updateState',
          payload:{
            allRecievedOrders:res.object,
            recievedTotal:res.totalElements
          }
        })
      }
    },
    * getAllOrdersByStatusSecond({payload},{call,select,put}){
      const res=yield call(getAllOrdersByStatus,payload)
      return res
    },
    * getProductSizeListByProduct({payload},{call,select,put}){
      const res=yield call(getProductSizeListByProduct,payload)
      console.log(res,"SIZELIST")
      if (res.success){
        yield put({
          type:'updateState',
          payload:{
            sizeListByProduct:res.object.productSizeList,
            selectedProduct:res.object
          }
        })
      }
      return res
    },
    * changeOrderStatus({payload},{call,select,put}){
      const res=yield call(changeOrderStatus,payload)
      return res
    },
    * saveOrder({payload},{call,select,put}){
      const res=yield call(saveOrder,payload)
      return res
    },
    * getAllProductsByCatAndBrand({payload},{call,select,put}){
      const res=yield call(getAllProductsByCatAndBrand,payload)
      console.log(res,"PRODUCTBYCATORBRAND")
      yield put({
        type:'updateState',
        payload:{
          allProductsByCatAndBrand:res.object
        }
      })
      return res
    }
  },
  reducers: {
    updateState(state, {payload}) {
      return {
        ...state,
        ...payload,
      }
    }
  }
}
