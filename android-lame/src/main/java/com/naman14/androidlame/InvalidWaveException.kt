package com.naman14.androidlame

import java.io.IOException

/**
 * @author banketree
 * @time 2020/1/15 10:55
 * @description
 * wave 自定义异常
 */
class InvalidWaveException : IOException {

    constructor() {}

    constructor(msg: String) : super(msg) {}

    companion object {
        /**
         * Generated serialVersionUID
         */
        private val serialVersionUID = -8229742633848759378L
    }
}