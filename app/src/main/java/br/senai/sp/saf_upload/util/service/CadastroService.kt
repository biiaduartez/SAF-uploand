package br.senai.sp.jandira.sistemadeuploadsaf.util.service

import retrofit2.Call
import br.senai.sp.jandira.sistemadeuploadsaf.util.model.ApiResponse
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface CadastroService {
    @POST("/usuario/cadastrarUsuario")
    fun postCadastro(@Body body: JsonObject): Call<ApiResponse>
}