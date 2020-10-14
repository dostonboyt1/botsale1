import api from 'services'

const {getAllBalance} = api
export default{
  namespace:'dashboard',
  state:{
    balance:[]
  },
  subscriptions:{},
  effects:{
    * getAllBalance({payload},{call,put,select}){
      const res=yield call(getAllBalance);
      console.log(res)
      yield put({
        type:'updateState',
        payload:{
          balance: res.object
        }
      })
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
