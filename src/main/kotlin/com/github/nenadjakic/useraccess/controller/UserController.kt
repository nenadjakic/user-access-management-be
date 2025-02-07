package com.github.nenadjakic.useraccess.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User controller", description = "API endpoints for user.")
@RestController
@RequestMapping("/user")
@Validated
class UserController {

}