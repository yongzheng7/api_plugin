/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atom.plugin.utils

/**
 * 扫描注册配置
 */
class ScanSetting {
    static final String PLUGIN_NAME = "com.atom.api"
    /**
     * 将apt自动生成的代码通过插件写入到该类中
     */
    static final String GENERATE_TO_CLASS_NAME = 'com/atom/runtime/AtomApi'
    /**
     * class权限定名
     */
    static final String GENERATE_TO_CLASS_FILE_NAME = GENERATE_TO_CLASS_NAME + '.class'
    /**
     * 代码写到了AtomApi类中的loadProxyClass方法
     */
    static final String GENERATE_TO_METHOD_NAME = 'loadProxyClass'
    /**
     * annotationProcessor自动生成路由代码的包名
     */
    static final String ROUTER_CLASS_PACKAGE_NAME = 'com/atom/apt'

    /**
     * 在AtomApi类中的loadProxyClass方法需要调用的方法
     */
    static final String REGISTER_METHOD_NAME = 'registerClass'
    /**
     * 需要扫描的接口名
     */
    String interfaceName = ''

    /**
     * 需要扫描的继承名
     */
    String superName = ''

    /**
     * 包含AtomApi类的jar包文件 {@link #GENERATE_TO_CLASS_NAME}
     */
    File fileContainsInitClass
    /**
     * 扫描结果 {@link #interfaceName}
     * @return 返回类名的集合
     */
    ArrayList<String> classList = new ArrayList<>()

    /**
     * 自动扫描注册的配置构造器
     * @param name 可能是接口也可能是继承类
     * @param isInterface true=接口  false=继承类
     */
    ScanSetting(String name , boolean isInterface){
        if(isInterface){
            this.interfaceName = name
        }else{
            this.superName = name
        }
    }

}