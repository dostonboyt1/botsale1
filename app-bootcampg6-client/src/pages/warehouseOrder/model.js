import api from 'services'

const {getAllOrdersByStatus,getAllSendOrders,changeToSend} = api

export default {
  namespace:'warehouseOrder',
  state:{
    approvedOrders:[],
    totalApproved:0,
    sendOrders:[],
    totalSend:0,
  },
  subscriptions:{},
  effects:{
    * getApprovedOrders({payload},{call,select,put}){
      const res=yield call(getAllOrdersByStatus,payload)
      yield put({
        type:'updateState',
        payload:{
          approvedOrders:res.object,
          totalApproved:res.totalElements
        }
      })
    },

    * getSendOrders({payload},{call,select,put}){
      const res=yield call(getAllSendOrders,payload)
      yield put({
        type:'updateState',
        payload:{
          sendOrders:res.object,
          totalSend:res.totalElements
        }
      })
    },
    * changeToSend({payload},{call,select,put}){
      const res=yield call(changeToSend,payload)
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
