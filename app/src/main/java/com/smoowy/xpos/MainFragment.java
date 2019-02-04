package com.smoowy.xpos;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
    TextView tTamano, tLote, tTamanoConversion, tLoteConversion, txCant, txPorcentaje, txConversion;
    double cantidad, porcentaje, referencia, tamanoPosicion, lote, tamanoPosicionC, loteC;
    ClipboardManager clipboard;
    Button bLimpiar, bLimpiarClaro;

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
        tTamanoConversion = view.findViewById(R.id.t_posicion_tamano_conversion);
        tLoteConversion = view.findViewById(R.id.t_posicion_lote_conversion);
        txCant = view.findViewById(R.id.text_cant);
        txPorcentaje = view.findViewById(R.id.text_porcen);
        txConversion = view.findViewById(R.id.texto_convers);
        txCant.setOnClickListener(onClickListener);
        txPorcentaje.setOnClickListener(onClickListener);
        txConversion.setOnClickListener(onClickListener);
        bLimpiar = view.findViewById(R.id.b_limpiar);
        bLimpiar.setOnClickListener(onClickListener);
        bLimpiarClaro = view.findViewById(R.id.b_limpiar_claro);
        bLimpiarClaro.setOnClickListener(onClickListener);


        return view;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.text_cant:
                    etCantidad.getText().clear();
                    break;

                case R.id.text_porcen:
                    etPorcentaje.getText().clear();
                    break;

                case R.id.texto_convers:
                    etReferencia.getText().clear();
                    break;

                case R.id.b_limpiar_claro:
                case R.id.b_limpiar: {
                    etCantidad.getText().clear();
                    etPorcentaje.getText().clear();
                    etReferencia.getText().clear();
                    tTamano.setText("TP");
                    tLote.setText("Lotes");
                    tTamanoConversion.setText("TPC");
                    tLoteConversion.setText("LC");
                    break;

                }
            }

        }
    };

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
                    tTamanoConversion.setText(String.format("%.0f", tamanoPosicionC));
                    tLoteConversion.setText(String.format("%.4f", loteC));
                } else {
                    tTamanoConversion.setText("TPC");
                    tLoteConversion.setText("LC");
                }


                lote = tamanoPosicion / 100000;
                tTamano.setText(String.format("%.0f", tamanoPosicion));
                tLote.setText(String.format("%.4f", lote));

            } else {

                tTamano.setText("TP");
                tLote.setText("Lotes");
                tTamanoConversion.setText("TPC");
                tLoteConversion.setText("LC");

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };


    private void copyToast(ClipboardManager clipboard, CharSequence text) {

        ClipData clip = ClipData.newPlainText("Precio", text.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Referencia grabada: " + text.toString(), Toast.LENGTH_SHORT).show();
    }

}
