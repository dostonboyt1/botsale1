import api from 'services'

const {getAllEmployeeByPageable,employeeSaveOrEdit,changeEnable,deleteEmployee} = api

export default {
  namespace:"employee",
  state:{
    allEmployeeByPageable:[],
    totalEmployeesAmount:0
  },
  subscriptions:{},
  effects:{
    * getAllEmployeeByPageable({payload},{call,select,put}){
      const res=yield call(getAllEmployeeByPageable,payload);
      console.log(res,"EMPLOYEE===RES")
      yield put({
        type:'updateState',
        payload:{
          allEmployeeByPageable:res.object,
          totalEmployeesAmount:res.totalElements
        }
      })
    },
    * saveOrEdit({payload},{call,select,put}){
      const res=yield call(employeeSaveOrEdit,payload)
      return res;
    },
    * changeEnable({payload},{call,select,put}){
      const res=yield call(changeEnable,payload)
      return res;
    },
    * deleteEmployee({payload},{call,select,put}){
      const res=yield call(deleteEmployee,payload)
      return res;
    }
  },
  reducers:{
    updateState(state, {payload}) {
      return {
        ...state,
        ...payload,
      }
    }
  }
}
