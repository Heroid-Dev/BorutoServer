package com.example


import com.example.heroRepository.HeroRepository
import com.example.heroRepository.NEXT_PAGE_KEY
import com.example.heroRepository.PREVIOUS_PAGE_KEY
import com.example.model.ApiResponse
import com.example.model.Hero
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("DEPRECATION")
class ApplicationTest {
    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `test root endpoint, assert corrent information`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Welcome to Boruto API!", response.content)
            }
        }
    }


    @Test
    fun `test access all heroes, query not existing page number, assert error`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=7").apply {
                assertEquals(HttpStatusCode.NoContent, response.status())
                val expected = ApiResponse(
                    success = false, message = "Heroes Not Found."
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                println("Expected:$expected")
                println("Actual:$actual")
                assertEquals(expected = expected, actual = actual)
            }
        }
    }


    @Test
    fun `test access all heroes, query not Number, assert error`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=invalid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                val expected = ApiResponse(
                    success = false,
                    message = "Only Number Enter."
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                println("Expected:${expected}")
                println("Actual:${actual}")
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun `test access all heroes, query hero name, assert single hero result`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sas").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes.size

                assertEquals(1, actual)
            }
        }
    }

    @Test
    fun `test access all heroes, query hero name, assert multiple hero result`() {
        val namesHero = mutableListOf<String>()
        withTestApplication(Application::module) {
            heroRepository.heroes.forEach { (_, hero) ->
                hero.forEach {
                    namesHero.add(it.name)
                }
            }
            namesHero.forEach {
                handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=${it}").apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    val expected = ApiResponse(
                        success = true,
                        message = "ok",
                        heroes = findHeroes(it)
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    println("NAME: $it")
                    println("Expected: ${expected.heroes.size}")
                    println("Actual: ${actual.heroes.size}")
                    assertEquals(expected.heroes.size, actual.heroes.size)
                }
            }

        }
    }

    private fun findHeroes(query: String?): List<Hero> {
        val founder = mutableListOf<Hero>()
        return if (!query.isNullOrEmpty()) {
            heroRepository.heroes.forEach { (_, heroes) ->
                heroes.forEach {
                    if (it.name.lowercase().contains(query.lowercase())) {
                        founder.add(it)
                    }
                }
            }
            founder
        } else {
            emptyList()
        }
    }

    @Test
    fun `test access all heroes, query hero name, assert multiple main hero result`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sa").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes.size

                assertEquals(3, actual)
            }
        }
    }

    @Test
    fun `test access all heroes, query hero name, assert empty list as a result`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes
                assertEquals(emptyList(), actual)
            }
        }
    }
    @Test
    fun `test access all heroes, query non existing name, assert empty list as a result`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=unKnown").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString()).heroes
                assertEquals(emptyList(), actual)
            }
        }
    }
    @Test
    fun `access non existing endpoint,assert not found`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/unKnown").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Error 404: Page Not Found!", response.content)
            }
        }
    }
    @Test
    fun `test access all heroes, query all page, assert corrent information`() {
        withTestApplication(Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "/boruto/heroes?page=${page}").apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    val expected = ApiResponse(
                        success = true,
                        message = "ok",
                        prevPage = calculatePageNumber(page)["prevPage"],
                        nextPage = calculatePageNumber(page)["nextPage"],
                        heroes = heroes[page - 1],
                        lastUpdate = actual.lastUpdate
                    )
                    println("CURRENT PAGE:${page}")
                    println("PREV_PAGE:${calculatePageNumber(page)["prevPage"]}")
                    println("NEXT_PAGE:${calculatePageNumber(page)["nextPage"]}")
                    println("HEROES:${heroes[page - 1]}")

                    assertEquals(expected, actual)
                }
            }
        }
    }

    private fun calculatePageNumber(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 5) {
            nextPage = null
        }
        if (page == 1) {
            prevPage = null
        }
        return mapOf(PREVIOUS_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }


}

