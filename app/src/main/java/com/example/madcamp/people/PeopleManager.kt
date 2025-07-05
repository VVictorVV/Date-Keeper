package com.example.madcamp.people

object PeopleManager {
    private val peopleList = mutableListOf<Person>()
    private var idCounter = 1L;

    fun getPeople(): List<Person> {
        return peopleList
    }

    fun addPerson(person: Person) {
        peopleList.add(person)
    }

    fun generateId(): Long {
        return idCounter++
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

    fun removePerson(person: Person) {
        peopleList.remove(person)
    }
}