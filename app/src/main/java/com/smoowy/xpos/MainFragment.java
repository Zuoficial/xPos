package com.smoowy.xpos;


import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainFragment extends Fragment {


    public MainFragment() {
    }

    EditText etCantidad, etPorcentaje, etReferencia, etCantidadMostrador;
    TextView tTamano, tLote, tTamanoC, tLoteC,
            tTituloCantidad, tTituloPorcentaje, tTituloConversion, tTituloTamano, tMargen, tMargenC,
            tSeguro, tSeguroC;
    double cantidad, porcentaje, referencia, tamanoPosicion,
            lote, tamanoPosicionC, loteC, margen, margenC, necesario, necesarioC,
            redondeoRef = 1000, ajusteRefRespaldo;
    int apalancamiento;
    int tipoApalancamiento = 4;
    boolean hayDecimales, yaRedondeo, resYaRedondeo, seAplanoLimpiar;
    ClipboardManager clipboard;
    Button bApalancamiento, bLimpiarClaro, bRedondeoDescendente,
            bRedondeoAscendente, bRegresarClaro, bRegresarRedondeo;
    SharedPreferences sharedPreferences;
    String resCantidad, resCantidadMostrador, resPorcentaje, resReferencia;
    String resCantidadRedo, resPorcentajeRedo, resReferenciaRedo;
    InputMethodManager inputMethodManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        etCantidad = view.findViewById(R.id.et_cantidad);
        etCantidadMostrador = view.findViewById(R.id.et_cantidad_mostrador);
        etCantidadMostrador.addTextChangedListener(textWatcherMostrador);
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
        bApalancamiento.setOnLongClickListener(onLongClickListenerApalancamiento);
        bLimpiarClaro = view.findViewById(R.id.b_limpiar_claro);
        bLimpiarClaro.setOnClickListener(onClickListener);
        bRedondeoDescendente = view.findViewById(R.id.b_redondeo_descendente);
        bRedondeoDescendente.setOnClickListener(onClickListener);
        bRedondeoDescendente.setOnLongClickListener(oLClickListenerRedondeoDescendente);
        bRedondeoAscendente = view.findViewById(R.id.b_redondeo_ascendente);
        bRedondeoAscendente.setOnClickListener(onClickListener);
        bRedondeoAscendente.setOnLongClickListener(oLClickListenerRedondeoAscendente);
        bRegresarClaro = view.findViewById(R.id.b_regresar_claro);
        bRegresarClaro.setOnClickListener(onClickListener);
        bRegresarRedondeo = view.findViewById(R.id.b_regresar_redondeo);
        bRegresarRedondeo.setOnClickListener(onClickListener);
        checadaSharedPreference();
        cambioApalancamiento();
        return view;

    }

    Dialog dialog;
    EditText etDialogReferencia;
    TextView tDialogTitulo;

    private void crearDialogRedondeo(boolean esAscendente) {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("Redondeo");
        if (redondeoRef % 1 == 0)
            etDialogReferencia.setText(String.format("%.0f", redondeoRef));
        else
            etDialogReferencia.setText(String.valueOf(redondeoRef));

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                redondeoRef = Double.parseDouble(etDialogReferencia.getText().toString());
                if (esAscendente)
                    ajusteRedondeo(true);
                else
                    ajusteRedondeo(false);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });

    }

    private void crearDialogApalancamiento() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.valueOf(apalancamiento));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("Leverage");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                apalancamiento = (int) Double.parseDouble(etDialogReferencia.getText().toString());
                tipoApalancamiento = 0;
                cambioApalancamiento();
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }

    @Override
    public void onDestroy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tipoApalancamiento", tipoApalancamiento);
        editor.putBoolean("hayDecimales", hayDecimales);
        editor.putString("cantidad", etCantidad.getText().toString());
        editor.putString("porcentaje", etPorcentaje.getText().toString());
        editor.putString("referencia", etReferencia.getText().toString());
        editor.putString("redondeoRef", String.valueOf(redondeoRef));
        editor.putInt("apalancamiento", apalancamiento);
        editor.apply();
        super.onDestroy();
    }


    View.OnLongClickListener oLClickListenerRedondeoDescendente = view -> {
        crearDialogRedondeo(false);
        return true;
    };

    View.OnLongClickListener oLClickListenerRedondeoAscendente = view -> {
        crearDialogRedondeo(true);
        return true;
    };

    View.OnLongClickListener onLongClickListenerApalancamiento = view -> {
        crearDialogApalancamiento();
        return true;
    };


    View.OnClickListener onClickListener = view -> {

        switch (view.getId()) {
            case R.id.t_titulo_cant:
                etCantidadMostrador.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_porcen:
                etPorcentaje.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_convers:
                etReferencia.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.b_limpiar_claro: {
                resCantidad = etCantidad.getText().toString();
                resCantidadMostrador = etCantidadMostrador.getText().toString();
                resPorcentaje = etPorcentaje.getText().toString();
                resReferencia = etReferencia.getText().toString();
                resYaRedondeo = yaRedondeo;
                etCantidadMostrador.getText().clear();
                etPorcentaje.getText().clear();
                etReferencia.getText().clear();
                ajusteRefRespaldo = redondeoRef;
                redondeoRef = 1000;
                tTamano.setText("TP");
                tLote.setText("Lotes");
                tTamanoC.setText("TPC");
                tLoteC.setText("LC");
                tMargen.setText("Margen");
                tMargenC.setText("MC");
                tSeguro.setText("Seguro");
                tSeguroC.setText("SC");
                yaRedondeo = false;
                seAplanoLimpiar = true;
                break;
            }

            case R.id.b_regresar_claro: {

                if (seAplanoLimpiar) {

                    if (resCantidad == null)
                        break;

                    etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(resCantidad)));
                    etCantidad.setText(resCantidad);
                    etPorcentaje.setText(resPorcentaje);
                    etReferencia.setText(resReferencia);
                    redondeoRef = ajusteRefRespaldo;
                    yaRedondeo = resYaRedondeo;
                    seAplanoLimpiar = false;
                }
                break;

            }


            case R.id.b_apalancamiento: {

                tipoApalancamiento += 1;

                if (tipoApalancamiento > 4)
                    tipoApalancamiento = 1;

                cambioApalancamiento();
                break;


            }

            case R.id.t_titulo_tamano: {
                hayDecimales = !hayDecimales;


                if (!tTamanoC.getText().toString().equals("TPC")) {
                    if (!hayDecimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else
                        tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                }

                if (!tTamano.getText().toString().equals("TP")) {
                    if (!hayDecimales)
                        tTamano.setText(String.format("%,.0f", tamanoPosicion));
                    else
                        tTamano.setText(String.format("%,.2f", tamanoPosicion));

                }
                break;

            }
            case R.id.b_redondeo_descendente:
                ajusteRedondeo(false);
                break;


            case R.id.b_redondeo_ascendente:
                ajusteRedondeo(true);
                break;

            case R.id.b_regresar_redondeo:
                if (resCantidadRedo == null)
                    break;

                etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(resCantidad)));
                etCantidad.setText(resCantidadRedo);
                etPorcentaje.setText(resPorcentajeRedo);
                etReferencia.setText(resReferenciaRedo);
                if (yaRedondeo)
                    yaRedondeo = false;
                break;


        }

    };

    private void ajusteRedondeo(boolean esAscendente) {
        double restante, restanteFinal, num,
                tamanoPosicioncAjustada, tamanoPosicionAjustada;

        resCantidadRedo = etCantidad.getText().toString();
        resPorcentajeRedo = etPorcentaje.getText().toString();
        resReferenciaRedo = etReferencia.getText().toString();

        if (!tTamanoC.getText().toString().equals("TPC")) {

            if (hayDecimales)
                tamanoPosicioncAjustada = tamanoPosicionC;
            else {

                tamanoPosicioncAjustada = Math.round(tamanoPosicionC);


                if (yaRedondeo) {
                    restante = tamanoPosicioncAjustada % redondeoRef;
                    tamanoPosicioncAjustada -= restante;

                    if (esAscendente) {
                        if (restante >= 1)
                            tamanoPosicioncAjustada += redondeoRef;
                    }
                }

            }


            restante = tamanoPosicioncAjustada % redondeoRef;
            restanteFinal = redondeoRef - restante;
            if (esAscendente)
                num = tamanoPosicioncAjustada + restanteFinal;

            else {
                num = tamanoPosicioncAjustada - restanteFinal;
            }

            num *= referencia;
            num *= (porcentaje / 100);

            if (num % 1 > 0) {
                etCantidadMostrador.setText(String.format("%.2f", num));
                etCantidad.setText(String.format("%.4f", num));

            } else {
                etCantidadMostrador.setText(String.format("%.0f", num));
            }


        } else if (!tTamano.getText().toString().equals("TP")) {


            if (hayDecimales)
                tamanoPosicionAjustada = tamanoPosicion;
            else {
                tamanoPosicionAjustada = Math.round(tamanoPosicion);

                if (yaRedondeo) {
                    restante = tamanoPosicionAjustada % redondeoRef;
                    tamanoPosicionAjustada -= restante;

                    if (esAscendente) {
                        if (restante >= 1)
                            tamanoPosicionAjustada += redondeoRef;
                    }
                }

            }

            restante = tamanoPosicionAjustada % redondeoRef;
            restanteFinal = redondeoRef - restante;

            if (esAscendente)
                num = tamanoPosicionAjustada + restanteFinal;

            else {
                num = tamanoPosicionAjustada - restanteFinal;
            }


            num *= (porcentaje / 100);

            if (num % 1 > 0) {
                etCantidadMostrador.setText(String.format("%.2f", num));
                etCantidad.setText(String.format("%.4f", num));

            } else {
                etCantidadMostrador.setText(String.format("%.0f", num));
            }

        }

        yaRedondeo = true;
    }


    private void cambioApalancamiento() {

        switch (tipoApalancamiento) {

            case 0:
                if (getLifecycle().getCurrentState().equals(Lifecycle.State.STARTED)) {
                    if (sharedPreferences.contains("apalancamiento"))
                        apalancamiento = sharedPreferences.getInt("apalancamiento", 100);
                }
                bApalancamiento.setText(String.valueOf(apalancamiento) + "X");
                break;

            case 1:
                apalancamiento = 100;
                bApalancamiento.setText("100X");
                break;

            case 2:
                apalancamiento = 200;
                bApalancamiento.setText("200X");
                break;

            case 3:
                apalancamiento = 400;
                bApalancamiento.setText("400X");
                break;

            case 4:
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
                    if (!hayDecimales)
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
                if (!hayDecimales)
                    tTamano.setText(String.format("%,.0f", tamanoPosicion));
                else
                    tTamano.setText(String.format("%,.2f", tamanoPosicion));
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
    TextWatcher textWatcherMostrador = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            etCantidad.setText(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checadaSharedPreference() {
        sharedPreferences = getActivity().getSharedPreferences("xPos", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("tipoApalancamiento"))
            tipoApalancamiento = sharedPreferences.getInt("tipoApalancamiento", 1);
        if (sharedPreferences.contains("hayDecimales"))
            hayDecimales = sharedPreferences.getBoolean("hayDecimales", false);
        if (sharedPreferences.contains("cantidad")) {

            if (sharedPreferences.getString("cantidad", "").isEmpty()) {
                etCantidadMostrador.setText("");
            } else {
                double valor = Double.valueOf(sharedPreferences.getString("cantidad", ""));
                etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(valor)));
                etCantidad.setText(String.format("%.4f", Double.valueOf(valor)));
            }
        }

        if (sharedPreferences.contains("porcentaje"))
            etPorcentaje.setText(sharedPreferences.getString("porcentaje", ""));
        if (sharedPreferences.contains("referencia"))
            etReferencia.setText(sharedPreferences.getString("referencia", ""));
        if (sharedPreferences.contains("redondeoRef"))
            redondeoRef = Double.parseDouble(sharedPreferences.getString("redondeoRef", "1000"));
        if (sharedPreferences.contains("apalancamiento"))
            apalancamiento = sharedPreferences.getInt("apalancamiento", 100);
    }


    private void copyToast(ClipboardManager clipboard, CharSequence text) {

        ClipData clip = ClipData.newPlainText("Precio", text.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Referencia grabada: " + text.toString(), Toast.LENGTH_SHORT).show();
    }


}
