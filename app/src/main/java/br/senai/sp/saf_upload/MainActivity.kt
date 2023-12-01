package br.senai.sp.saf_upload

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.senai.sp.jandira.sistemadeuploadsaf.util.model.ApiResponse
import br.senai.sp.jandira.sistemadeuploadsaf.util.service.CadastroService
import br.senai.sp.jandira.sistemadeuploadsaf.util.service.RetrofitFactory
import br.senai.sp.saf_upload.ui.theme.SAFuploadTheme
import br.senai.sp.saf_upload.util.StorageUtil
import coil.compose.AsyncImage
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {
    private lateinit var apiService: CadastroService
    override fun onCreate(savedInstanceState: Bundle?) {
//        apiService = RetrofitFactory.getInstance().create(apiService::class.java)
        super.onCreate(savedInstanceState)

        setContent {
            SAFuploadTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SinglePhotoPicker()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePhotoPicker() {


    var uri by remember {
        mutableStateOf<Uri?>(null)
    }

    var url by remember {
        mutableStateOf<String>("")
    }

    var email by remember {
        mutableStateOf<String>("")
    }

    var senha by remember {
        mutableStateOf<String>("")
    }

    var results by remember {
        mutableStateOf<ApiResponse?>(null)
    }


    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 12.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            contentAlignment = Alignment.BottomEnd

        ) {

            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clickable {
                        singlePhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                shape = CircleShape,
            ) {
                AsyncImage(
                    model = uri ?: R.drawable.baseline_person_24,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                DisposableEffect(uri) {
                    if (uri != null) {
                        // Call your UploadPick function here when uri is updated
                        fun UploadPick(uri: Uri, context: Context) {
                            uri?.let {
                                StorageUtil.uploadToStorage(
                                    uri = it,
                                    context = context,
                                    type = "image",
                                    {
                                        url = it
                                    })
                            }
                        }

                        UploadPick(uri!!, context)

                    }

                    onDispose { }
                }
            }
            Image(
                painterResource(id = R.drawable.add_a_photo),
                contentDescription = "",
                modifier = Modifier.size(height = 32.dp, width = 32.dp)
            )
        }
        Text(text = "$url")
        Column {
            OutlinedTextField(
                value = email ?: "example@gmail.com",
                shape = RoundedCornerShape(16.dp),
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp),
                label = { Text(text = "E-MAIL") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_email_24),
                        contentDescription = "Username",
                        modifier = Modifier,
                        tint = Color(215, 215, 255)
                    )
                },

                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        focusedBorderColor = Color(220, 215, 255),
                        focusedLabelColor = Color(220, 215, 255)
                    )
            )

            OutlinedTextField(
                value = senha ?: "example@gmail.com",
                shape = RoundedCornerShape(16.dp),
                onValueChange = { senha = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp),
                label = { Text(text = "SENHA") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_lock_24),
                        contentDescription = "Username",
                        modifier = Modifier,
                        tint = Color(215, 215, 255)
                    )
                },
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        focusedBorderColor = Color(220, 215, 255),
                        focusedLabelColor = Color(220, 215, 255)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val body = JsonObject().apply {
                        addProperty("login", email)
                        addProperty("senha", senha)
                        addProperty("imagem", url)
                    }

                    val call = RetrofitFactory().postCadastro().postCadastro(body)

                    call.enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(
                            call: Call<ApiResponse>,
                            response: Response<ApiResponse>
                        ) {
//                            results = response.body()!!.
                            Toast.makeText(
                                context,
                                response.body()!!.mensagemStatus,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Log.i(
                                "ds2m",
                                "onFailure: ${t.message}"
                            )

                            Log.i(
                                "ds2m",
                                "onFailure: ${call}"
                            )
                        }

                    })



                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(225, 225, 255)),

                ) {
                Text(text = "Cadastrar")
            }
        }
    }



}





@Preview(showSystemUi = true, showBackground = true)
@Composable
fun UploadFirebasePreview() {
    SinglePhotoPicker()
}