package com.example.myapplication.Model.Repository.OpenWeatherRestApiSource.Network

import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException


suspend fun <ResponseType, NetworkResponseType> restApiRequest(
    mapper: (NetworkResponseType) -> ResponseType,
    network: suspend () -> Response<NetworkResponseType>
): ResponseType {
    return try {
        val response = network()
        val responseCode = response.code()
        if (response.isSuccessful) {
            response.body()?.let {
                return mapper(it)
            } ?: throw error("Response Code: $responseCode response Body is Null!")
        } else {
            throw error("Response Code: $responseCode" + response.errorBody()?.toString())
        }
    } catch (e: UnknownHostException) {
        throw error(message = e.message ?: "Something went wrong")
    } catch (e: SocketTimeoutException) {
        throw error(message = e.message ?: "Something went wrong")
    } catch (e: Exception) {
        throw error(message = e.message ?: "Something went wrong")
    }
}