export default {

  login: 'POST /auth/login',
  userMe:'/user/me',

  getCatByPageable:'/category/getAllCategoriesByPageable',
  getAllCategories:'/category/all',
  categorySaveOrEdit:'POST /category',
  deleteCategory:'DELETE /category',

  getAllEmployeeByPageable:'/user/byPageable',
  employeeSaveOrEdit:'POST /auth/register',
  changeEnable:'/user/changeEnable',
  deleteEmployee:'DELETE /user',

  getAllWarehouse:'/warehouse/all',
  getAllRegions:'/warehouse/allRegion',
  getAllDistrict:'/warehouse/allDistrict',
  getAllUsers:'/user/all',
  warehouseSaveOrEdit:'POST /warehouse',
  deleteWarehouse:'DELETE /warehouse',

  getBrandByPageable:'/brand/byPageable',
  brandSaveOrEdit:'POST /brand',
  changeActiveBrand:'/brand/changeActive',
  brandDelete:'DELETE /brand',

  getProductSizeByPageable:'/productSize/byPageable',
  saveOrEditProductSize:'POST /productSize',
  deleteProductSize:'DELETE /productSize',

  getProductByPageable:'/product/search',
  productSaveOrEdit:'POST /product',
  deleteProduct:'DELETE /product',
  changeActive:'/product/changeActive',
  getAllProductsByCatAndBrand:'/product/byCatIdOrBrandId',
  getProductSizeListByProduct:'/product/sizeListByProduct',

  getAllRegion:'/region/all',
  regionSaveOrEdit:'POST /region',
  deleteRegion:'DELETE /region',

  getAllDistricts:'/district/getAll',
  districtGetByPagable:'/district/getPagaeble',
  districtSaveOrEdit:'POST /district',
  deleteDistrict:'DELETE /district',

  addOrEditFile:'POST /file',
  getAllBrands:'/brand/all',
  getAllProductSize:'/productSize/all',

  getAllInputsByPageable:'/output/byConfirmedAndIsIncome',
  saveInputOrOutput:'POST /output/inputOrOutput',
  removeInputOrOutput:'DELETE /output/',
  confirmInput:'/output/confirm',

  getAllWarehouseWithoutOutputer:'/warehouse/getAllWarehouseWithoutOutputer',

  getAllOrdersByStatus:'/order/byORderStatus',
  changeOrderStatus:'/order/changeOrderStatus',
  getAllSendOrders:'/order/bySendOrdersByWarehouse',
  changeToSend:'/order/changeToSend',
  saveOrder:'POST /order',

  getAllPayTypes:'/payType',

  getAllBalance:'/dashboard/warehouseBalance'
}
