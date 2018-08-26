package com.powertrip

import org.bson.types.ObjectId


package object api {
  case class FindByIdRequest(id: String) {
    require(ObjectId.isValid(id), "the informed id is not a representation of a valid hex string")
  }
}
