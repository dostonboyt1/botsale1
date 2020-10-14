
// ref: https://umijs.org/config/
import {resolve} from "path";

export default {
  treeShaking: true,
  plugins: [
    // ref: https://umijs.org/plugin/umi-plugin-react.html
    ['umi-plugin-react', {
      antd: true,
      dva: true,
      dynamicImport: true,
      title: 'bootcampg6',
      dll: false,

      routes: {
        exclude: [
          /models\//,
          /services\//,
          /model\.(t|j)sx?$/,
          /service\.(t|j)sx?$/,
          /components\//,
        ],
      },
    }],
    [
      'umi-plugin-react',
      {
        locale: {
          default: 'zh-CN', //默认语言 zh-CN，如果 baseSeparator 设置为 _，则默认为 zh_CN
          baseNavigator: true, // 为true时，用navigator.language的值作为默认语言
          antd: true, // 是否启用antd的<LocaleProvider />
          baseSeparator: '-', // 语言默认分割符 -
        },
      },
    ],
  ],
  cssLoaderOptions:{
    localIdentName:"[local]"
  },
  proxy: {
    "/api": {
      "target": "http://localhost",
      "changeOrigin": true
    }
  },
  alias: {
    api: resolve(__dirname, './src/services/'),
    utils: resolve(__dirname, "./src/utils"),
    services: resolve(__dirname, "./src/services"),
    components:resolve(__dirname,"./src/components"),
    config: resolve(__dirname, "./src/utils/config"),

  },
  outputPath:'../app-accessory-server/src/main/resources/static'
}
