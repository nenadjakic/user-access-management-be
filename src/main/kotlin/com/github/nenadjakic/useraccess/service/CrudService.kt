package com.github.nenadjakic.useraccess.service

interface CrudService<T, ID> : WriteService<T, ID>, ReadService<T, ID>