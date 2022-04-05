package com.fistkim.hazelcastsample.model

// 기본 생성자 만들어주기 위해서는 모든 필드에 기본값 세팅해줘야한다
// 이런 방식 아니면 다른 방식으로라도 기본생성자를 만들어줘야 CBOR 오 역직렬화가 가능(역직렬화시 기본 생성자 사용한다)
// https://stackoverflow.com/questions/37873995/how-to-create-empty-constructor-for-data-class-in-kotlin-android
data class Person(val name: String? = null)
