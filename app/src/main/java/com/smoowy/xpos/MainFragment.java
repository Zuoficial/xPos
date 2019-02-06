package com.smoowy.xpos;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }

    EditText etCantidad, etPorcentaje, etReferencia;
    TextView tTamano, tLote, tTamanoC, tLoteC,
            tTituloCantidad, tTituloPorcentaje, tTituloConversion, tTituloTamano, tMargen, tMargenC,
            tSeguro, tSeguroC;
    double cantidad, porcentaje, referencia, tamanoPosicion,
            lote, tamanoPosicionC, loteC, margen, margenC, necesario, necesarioC;
    int apalancamiento;
    int tipoApalancamiento = 9;
    boolean decimales;
    ClipboardManager clipboard;
    Button bApalancamiento, bLimpiarClaro;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        etCantidad = view.findViewById(R.id.et_cantidad);
        etPorcentaje = view.findViewById(R.id.et_porcentaje);
        etReferencia = view.findViewById(R.id.et_referencia);
        etCantidad.addTextChangedListener(textWatcher);
        etPorcentaje.addTextChangedListener(textWatcher);
        etReferencia.addTextChangedListener(textWatcher);
        tTamano = view.findViewById(R.id.t_posicion_tamano);
        tLote = view.findViewById(R.id.t_posicion_lote);
        tTamanoC = view.findViewById(R.id.t_posicion_tamano_conversion);
        tLoteC = view.findViewById(R.id.t_posicion_lote_conversion);
        tTituloCantidad = view.findViewById(R.id.t_titulo_cant);
        tTituloPorcentaje = view.findViewById(R.id.t_titulo_porcen);
        tTituloConversion = view.findViewById(R.id.t_titulo_convers);
        tTituloTamano = view.findViewById(R.id.t_titulo_tamano);
        tTituloCantidad.setOnClickListener(onClickListener);
        tTituloPorcentaje.setOnClickListener(onClickListener);
        tTituloConversion.setOnClickListener(onClickListener);
        tTituloTamano.setOnClickListener(onClickListener);
        tSeguro = view.findViewById(R.id.t_seguro);
        tSeguroC = view.findViewById(R.id.t_seguroC);
        tMargen = view.findViewById(R.id.t_margen);
        tMargenC = view.findViewById(R.id.t_margenC);
        bApalancamiento = view.findViewById(R.id.b_apalancamiento);
        bApalancamiento.setOnClickListener(onClickListener);
        bLimpiarClaro = view.findViewById(R.id.b_limpiar_claro);
        bLimpiarClaro.setOnClickListener(onClickListener);
        registerForContextMenu(bApalancamiento);
        checadaSharedPreference();
        cambioApalancamiento();
        return view;
    }


    @Override
    public void onDestroy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tipoApalancamiento", tipoApalancamiento);
        editor.putBoolean("decimales", decimales);
        editor.putString("cantidad", etCantidad.getText().toString());
        editor.putString("porcentaje", etPorcentaje.getText().toString());
        editor.putString("referencia", etReferencia.getText().toString());
        editor.apply();
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case R.id.menu_3x:
                tipoApalancamiento = 0;
                break;

            case R.id.menu_5x:
                tipoApalancamiento = 1;
                break;

            case R.id.menu_10x:
                tipoApalancamiento = 2;
                break;

            case R.id.menu_20x:
                tipoApalancamiento = 3;
                break;

            case R.id.menu_25x:
                tipoApalancamiento = 4;
                break;

            case R.id.menu_50x:
                tipoApalancamiento = 5;
                break;

            case R.id.menu_100x:
                tipoApalancamiento = 6;
                break;

            case R.id.menu_200x:
                tipoApalancamiento = 7;
                break;

            case R.id.menu_400x:
                tipoApalancamiento = 8;
                break;

            case R.id.menu_500x:
                tipoApalancamiento = 9;
                break;
        }
        cambioApalancamiento();
        return super.onContextItemSelected(item);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.t_titulo_cant:
                    etCantidad.getText().clear();
                    break;

                case R.id.t_titulo_porcen:
                    etPorcentaje.getText().clear();
                    break;

                case R.id.t_titulo_convers:
                    etReferencia.getText().clear();
                    break;

                case R.id.b_limpiar_claro: {
                    etCantidad.getText().clear();
                    etPorcentaje.getText().clear();
                    etReferencia.getText().clear();
                    tTamano.setText("TP");
                    tLote.setText("Lotes");
                    tTamanoC.setText("TPC");
                    tLoteC.setText("LC");
                    tMargen.setText("Margen");
                    tMargenC.setText("MC");
                    tSeguro.setText("Seguro");
                    tSeguroC.setText("SC");
                    break;
                }
                case R.id.b_apalancamiento: {

                    tipoApalancamiento += 1;

                    if (tipoApalancamiento > 9)
                        tipoApalancamiento = 0;

                    cambioApalancamiento();
                    break;


                }

                case R.id.t_titulo_tamano: {
                    decimales = !decimales;


                    if (!tTamanoC.getText().toString().equals("TPC")) {
                        if (!decimales)
                            tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                        else
                            tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                    }
                    break;

                }
            }

        }
    };

    private void cambioApalancamiento() {

        switch (tipoApalancamiento) {

            case 0:
                apalancamiento = 3;
                bApalancamiento.setText("3X");
                break;

            case 1:
                apalancamiento = 5;
                bApalancamiento.setText("5X");
                break;
            case 2:
                apalancamiento = 10;
                bApalancamiento.setText("10X");
                break;

            case 3:
                apalancamiento = 20;
                bApalancamiento.setText("20X");
                break;

            case 4:
                apalancamiento = 25;
                bApalancamiento.setText("25X");
                break;

            case 5:
                apalancamiento = 50;
                bApalancamiento.setText("50X");
                break;

            case 6:
                apalancamiento = 100;
                bApalancamiento.setText("100X");
                break;

            case 7:
                apalancamiento = 200;
                bApalancamiento.setText("200X");
                break;

            case 8:
                apalancamiento = 400;
                bApalancamiento.setText("400X");
                break;

            case 9:
                apalancamiento = 500;
                bApalancamiento.setText("500X");
                break;

        }

        if (!tTamano.getText().toString().equals("TP")) {

            margen = tamanoPosicion / apalancamiento;
            necesario = cantidad + margen;
            tMargen.setText(String.format("%,.2f", margen));
            tSeguro.setText(String.format("%,.2f", necesario));

            if (!tTamanoC.getText().toString().equals("TPC")) {
                margenC = tamanoPosicionC / apalancamiento;
                necesarioC = (cantidad / referencia) + margenC;
                tMargenC.setText(String.format("%,.2f", margenC));
                tSeguroC.setText(String.format("%,.2f", necesarioC));

            }
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            if (!etCantidad.getText().toString().isEmpty() &&
                    !etCantidad.getText().toString().equals(".") &&
                    !etPorcentaje.getText().toString().isEmpty() &&
                    !etPorcentaje.getText().toString().equals(".")

            ) {
                cantidad = Double.parseDouble(etCantidad.getText().toString());
                porcentaje = Double.parseDouble(etPorcentaje.getText().toString());
                tamanoPosicion = cantidad / (porcentaje / 100);


                if (!etReferencia.getText().toString().isEmpty() &&
                        !etReferencia.getText().toString().equals(".")) {

                    referencia = Double.parseDouble(etReferencia.getText().toString());
                    tamanoPosicionC = tamanoPosicion / referencia;
                    loteC = tamanoPosicionC / 100000;
                    margenC = tamanoPosicionC / apalancamiento;
                    necesarioC = (cantidad / referencia) + margenC;
                    if (!decimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else
                        tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                    tLoteC.setText(String.format("%.4f", loteC));
                    tMargenC.setText(String.format("%,.2f", margenC));
                    tSeguroC.setText(String.format("%,.2f", necesarioC));
                } else {

                    if (!tTamanoC.getText().toString().equals("TPC")) {
                        tTamanoC.setText("TPC");
                        tLoteC.setText("LC");
                        tMargenC.setText("MC");
                        tSeguroC.setText("SC");
                    }
                }


                lote = tamanoPosicion / 100000;
                margen = tamanoPosicion / apalancamiento;
                necesario = margen + cantidad;
                tTamano.setText(String.format("%,.0f", tamanoPosicion));
                tLote.setText(String.format("%.4f", lote));
                tMargen.setText(String.format("%,.2f", margen));
                tSeguro.setText(String.format("%,.2f", necesario));


            } else {

                if (!tTamano.getText().toString().equals("TP")) {
                    tTamano.setText("TP");
                    tLote.setText("Lotes");
                    tTamanoC.setText("TPC");
                    tLoteC.setText("LC");
                    tMargen.setText("Margen");
                    tMargenC.setText("MC");
                    tSeguro.setText("Seguro");
                    tSeguroC.setText("SC");
                }

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };

    private void checadaSharedPreference() {
        sharedPreferences = getActivity().getSharedPreferences("xPos", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("tipoApalancamiento"))
            tipoApalancamiento = sharedPreferences.getInt("tipoApalancamiento", 9);
        if (sharedPreferences.contains("decimales"))
            decimales = sharedPreferences.getBoolean("decimales", false);
        if (sharedPreferences.contains("cantidad"))
            etCantidad.setText(sharedPreferences.getString("cantidad", ""));
        if (sharedPreferences.contains("porcentaje"))
            etPorcentaje.setText(sharedPreferences.getString("porcentaje", ""));
        if (sharedPreferences.contains("referencia"))
            etReferencia.setText(sharedPreferences.getString("referencia", ""));
    }



    private void copyToast(ClipboardManager clipboard, CharSequence text) {

        ClipData clip = ClipData.newPlainText("Precio", text.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Referencia grabada: " + text.toString(), Toast.LENGTH_SHORT).show();
    }

}
