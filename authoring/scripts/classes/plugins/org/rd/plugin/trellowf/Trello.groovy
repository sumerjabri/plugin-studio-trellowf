package plugins.org.rd.plugin.trellowf

import groovy.json.JsonSlurper

/**
 * API service wrapper for Trello
 */
public class Trello {

    def pluginConfig
    def key
    def token
    def defaultBoardId

   /**
     * constructor
     */
    Trello(pluginConfig) {
        this.pluginConfig = pluginConfig
        this.key = pluginConfig.getString("trelloApiKey")
        this.token = pluginConfig.getString("trelloApiToken")
        this.defaultBoardId = pluginConfig.getString("trelloDefaultBoardId")
    }

    /**
     * Get board details
     * @param boardId the board to retrieve
     */
    def getBoard(boardId) {
        def tBoardId = getBoardId(boardId)
        def board = trelloGet("/1/boards/${tBoardId}")
        return board
    }

    /**
     * Get all of the lists on a given board
     * @param boardId the board to retrieve
     */
    def getListsForBoard(boardId) { 
        def result = [:]

        def tBoardId = getBoardId(boardId)

        result.board = getBoard(tBoardId)

        result.lists = trelloGet("/1/boards/${tBoardId}/lists")

        result.lists.each { list -> 
            def cards = getCardsForList(list.id)
            list.cards = cards
        }

        return result
    }

    /**
     * If boardId is null, default ID is returned
     */
    def getBoardId(boardId) {

        def id = boardId
        if(!boardId || boardId == null || boardId == "") {
            // Use the default board ID if one is not provided
            id = defaultBoardId
        }

        return id
    }

    /**
     * Get all of the cards on a given list
     * @param listId the list of cards to retrieve
     */
    def getCardsForList(listId) {
        def cards = trelloGet("/1/lists/${listId}/cards")
        return cards
    }

    /**
     * Make a get request to Trello
     * @param url - the API URL
     */
    def trelloGet(url) {
        def apiUrl = "https://api.trello.com${url}?key=${key}&token=${token}"
        def json = new URL(apiUrl).text
        def object = new JsonSlurper().parseText(json) 
        return object
    }
}