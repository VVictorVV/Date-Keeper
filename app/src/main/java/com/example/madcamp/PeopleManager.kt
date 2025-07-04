package com.example.madcamp

import com.example.madcamp.data.Person

object PeopleManager {
    private val peopleList = mutableListOf<Person>()

    fun getPeople(): List<Person> {
        return peopleList
    }

    fun addPerson(person: Person) {
        peopleList.add(person)
    }

    fun getPersonById(id: Long): Person? {
        return peopleList.find { it.id == id }
    }

    fun updatePerson(updatedPerson: Person) {
        val index = peopleList.indexOfFirst { it.id == updatedPerson.id }
        if (index != -1) {
            peopleList[index] = updatedPerson
        }
    }

    fun deletePerson(personId: Long) {
        peopleList.removeIf { it.id == personId }
    }
}