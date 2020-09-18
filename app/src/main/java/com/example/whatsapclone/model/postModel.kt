package com.example.whatsapclone.model

import java.util.*
import kotlin.collections.ArrayList

class postModel {
    public var image : String?= null
    public var title : String?= null
    public var body : String?= null
    var color:Int ?=null
    var option1list:ArrayList<String> ?=null
    var option2list:ArrayList<String> ?=null
    var option3list:ArrayList<String> ?=null
    var optionslist:ArrayList<tagModel> ?=null

    public var date : Date?= null
    public var userid : String?= null
    var tag:ArrayList<tagModel> ?=null
}