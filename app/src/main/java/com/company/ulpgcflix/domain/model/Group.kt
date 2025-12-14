package com.company.ulpgcflix.domain.model

data class Group(
    private  val id: String,
    private  val name: String,
    private  val image:String,
    private  val ownerId:String,
    private  var description:String,
    private var isPublic:Boolean=false,
){

    val getIsPublic: Boolean
        get() = this.isPublic


    val getId: String
        get() = this.id

    val getDescription: String
        get() = this.description

    val getName: String
        get() = this.name

    val getIdOwner: String
        get() = this.ownerId

    val getImage: String
        get() = this.image
}