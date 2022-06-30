package com.example.android_mysql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android_mysql.databinding.ActivityMainBinding
import org.json.JSONException

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var binding:ActivityMainBinding
    var idGlobal: String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGuardar.setOnClickListener(this)
        binding.btnEditarMain.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)

        CargaTabla()




    }
    fun CargaTabla(){
        binding.tbUsuarios.removeAllViews()
        var queue=Volley.newRequestQueue(this)
        var url="http://192.168.10.19/android_mysql/registros.php"
        var jsonObjectRequest=JsonObjectRequest(Request.Method.GET,url,null,
            Response.Listener { response ->
                try {
                    var jsonArray=response.getJSONArray("data")
                    for(i in 0 until jsonArray.length()){
                        var jsonObject=jsonArray.getJSONObject(i)
                        val registro=LayoutInflater.from(this).inflate(R.layout.table_row_p,null,false)
                        val colNombre= registro.findViewById<View>(R.id.colNombre) as TextView
                        val colEmail = registro.findViewById<View>(R.id.colEmail) as TextView
                        val colEditar = registro.findViewById<View>(R.id.colEditar)
                        val colBorrar= registro.findViewById<View>(R.id.colBorrar)

                        colNombre.text=jsonObject.getString("nombre")
                        colEmail.text=jsonObject.getString("email")
                        colEditar.id=jsonObject.getString("id").toInt()
                        colBorrar.id=jsonObject.getString("id").toInt()

                        binding.tbUsuarios?.addView(registro)
                    }

                }catch (e:JSONException){
                    e.printStackTrace()
                }

            },Response.ErrorListener { error ->

            })
        queue.add(jsonObjectRequest)

    }
    fun EditarGuardar(){
        val url = "http://192.168.10.19/android_mysql/editar.php"
        val queue=Volley.newRequestQueue(this)
        val resultadoPost=object :StringRequest(Request.Method.POST,url,
            Response.Listener { response ->
                Toast.makeText(this,"El usuario se edito Correctamente",Toast.LENGTH_LONG).show();
                CargaTabla()
            },Response.ErrorListener { error ->
                Toast.makeText(this,"Error al editar el usuario $error",Toast.LENGTH_LONG).show();
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String, String>()
                parametros.put("id",idGlobal!!)
                parametros.put("nombre",binding.edtNombre.text.toString())
                parametros.put("telefono",binding.edtTelefono.text.toString())
                parametros.put("email",binding.edtEmail.text.toString())
                parametros.put("pass",binding.edtPass.text.toString())
                return parametros}
        }
        queue.add(resultadoPost)
    }

    fun clickTablaEditar(view: View){
        idGlobal=view.id.toString()
        val queue=Volley.newRequestQueue(this)
        val url = "http://192.168.10.19/android_mysql/registro.php?id=${idGlobal}"
        val jsonObjectRequest=JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                binding.edtNombre?.setText(response.getString("nombre"))
                binding.edtEmail?.setText(response.getString("email"))
                binding.edtTelefono?.setText(response.getString("telefono"))
                binding.edtPass?.setText(response.getString("pass"))

            },Response.ErrorListener { error ->
                Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)

    }
    fun clickTablaBorrar(view: View){
        val url = "http://192.168.10.19/android_mysql/borrar.php"
        val queue=Volley.newRequestQueue(this)
        val resultadoPost= object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this,"El usuario se elimino de forma exitosa", Toast.LENGTH_LONG).show();
                CargaTabla()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al eliminar el usuario $error", Toast.LENGTH_LONG).show();
            }
        ){
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String, String>()
                parametros.put("id",view.id.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
        //Toast.makeText(this,view.id.toString(),Toast.LENGTH_LONG).show()
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.btnGuardar -> Insertar()
            R.id.btnEditarMain ->EditarGuardar()
            R.id.btnReset -> Resetear()
        }
    }

    private fun Insertar() {
        val url = "http://192.168.10.19/android_mysql/insertar.php"
        val queue=Volley.newRequestQueue(this)
        var resultadoPost= object : StringRequest(Request.Method.POST,url,
        Response.Listener<String> { response ->
            Toast.makeText(this,"Usuario registrado exitosamente",Toast.LENGTH_LONG).show()
            CargaTabla()
        },Response.ErrorListener { error ->
                Toast.makeText(this,"Error $error",Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()

                parametros.put("nombre",binding.edtNombre?.text.toString())
                parametros.put("email",binding.edtEmail?.text.toString())
                parametros.put("telefono",binding.edtTelefono?.text.toString())
                parametros.put("pass",binding.edtPass?.text.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
    }

    fun Resetear(){
        binding.edtNombre.setText("")
        binding.edtTelefono.setText("")
        binding.edtEmail.setText("")
        binding.edtPass.setText("")
    }


}