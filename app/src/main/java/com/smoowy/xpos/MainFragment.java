package com.smoowy.xpos;


import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
    double cantidad, porcentajeEntero, referencia, tamanoPosicion,
            lote, tamanoPosicionC, loteC, margen, margenC, necesario, necesarioC,
            redondeoRef = 1000, ajusteRefRespaldo, resPrecioXDialogPos, resPorcentajeXDialogPos;
    int apalancamiento;
    int tipoApalancamiento = 4;
    boolean hayDecimales, yaRedondeo, resYaRedondeo, seAplanoLimpiar;
    ClipboardManager clipboard;
    Button bApalancamiento, bLimpiarClaro, bRedondeoDescendente,
            bRedondeoAscendente, bRegresarClaro, bPR, bXT;
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
        tTamano.setOnClickListener(clickListenerPosicion);
        tLote = view.findViewById(R.id.t_posicion_lote);
        tLote.setOnClickListener(clickListenerPosicion);
        tTamanoC = view.findViewById(R.id.t_posicion_tamano_conversion);
        tTamanoC.setOnClickListener(clickListenerPosicion);
        tLoteC = view.findViewById(R.id.t_posicion_lote_conversion);
        tLoteC.setOnClickListener(clickListenerPosicion);
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
        tMargen.setOnClickListener(clickListenerPosicion);
        tMargenC = view.findViewById(R.id.t_margenC);
        tMargenC.setOnClickListener(clickListenerPosicion);
        bApalancamiento = view.findViewById(R.id.b_apalancamiento);
        bApalancamiento.setOnClickListener(onClickListener);
        bApalancamiento.setOnLongClickListener(onLongClickListener);
        bLimpiarClaro = view.findViewById(R.id.b_limpiar_claro);
        bLimpiarClaro.setOnClickListener(onClickListener);
        bRedondeoDescendente = view.findViewById(R.id.b_redondeo_descendente);
        bRedondeoDescendente.setOnClickListener(onClickListener);
        bRedondeoDescendente.setOnLongClickListener(onLongClickListener);
        bRedondeoAscendente = view.findViewById(R.id.b_redondeo_ascendente);
        bRedondeoAscendente.setOnClickListener(onClickListener);
        bRedondeoAscendente.setOnLongClickListener(onLongClickListener);
        bRegresarClaro = view.findViewById(R.id.b_regresar_claro);
        bRegresarClaro.setOnClickListener(onClickListener);
        bPR = view.findViewById(R.id.b_pr);
        bPR.setOnClickListener(onClickListener);
        bXT = view.findViewById(R.id.b_xt);
        bXT.setOnClickListener(onClickListener);
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


    private void crearDialogTamano() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        if (hayDecimales)
            etDialogReferencia.setText(String.format("%.2f", tamanoPosicion));
        else
            etDialogReferencia.setText(String.format("%.0f", tamanoPosicion));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("TP");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    private void crearDialogTamanoConversion() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        if (hayDecimales)
            etDialogReferencia.setText(String.format("%.2f", tamanoPosicionC));
        else
            etDialogReferencia.setText(String.format("%.0f", tamanoPosicionC));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("TPC");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= referencia;
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    private void crearDialogLotes() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.4f", tamanoPosicion / 100000));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("Lotes");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= 100000;
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }

    private void crearDialogLotesConversion() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.4f", tamanoPosicionC / 100000));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("LC");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= 100000;
                num *= referencia;
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    boolean esCantidadEnDialogMargen = true;
    EditText etCantidadDialogMargen, etPorcentajeDialogMargen, etPrecisionDialogMargen;
    TextView tMargenDialogMargen, tTituloDialogMargen,
            tTituloCantidadDialogMargen, tTituloPorcentajeDialogMargen;
    Button bLimpiarDialogMargen, bRegresarDialogMargen, bSalirDialogMargen;
    String resCantidadDialogMargin, resPorcentajeDialogMargin, formatoDialogMargen;
    double numDialogMargen;
    boolean seAplanoLimpiarDialogMargen;

    private void crearDialogMargen(boolean esDialogMargenConversion) {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog_margen);
        dialog.show();
        etCantidadDialogMargen = dialog.findViewById(R.id.et_cantidad_dialog_margen);
        etPorcentajeDialogMargen = dialog.findViewById(R.id.et_porcentaje_dialog_margen);
        etPrecisionDialogMargen = dialog.findViewById(R.id.et_precision_dialog_margen);
        tTituloDialogMargen = dialog.findViewById(R.id.t_titulo_margen_dialog_margen);
        tMargenDialogMargen = dialog.findViewById(R.id.t_margen_dialog_margen);
        tTituloCantidadDialogMargen = dialog.findViewById(R.id.t_titulo_cantidad_dialog_margen);
        tTituloPorcentajeDialogMargen = dialog.findViewById(R.id.t_titulo_porcen_dialog_margen);
        bLimpiarDialogMargen = dialog.findViewById(R.id.b_limpiar_dialog_margen);
        bRegresarDialogMargen = dialog.findViewById(R.id.b_regresar_dialog_margen);
        bSalirDialogMargen = dialog.findViewById(R.id.b_salir_dialog_margen);

        if (!esDialogMargenConversion) {

            etCantidadDialogMargen.setText(String.format("%.2f", margen));
            tMargenDialogMargen.setText(String.format("%.2f", margen));

            if (esCantidadEnDialogMargen) {
                tTituloDialogMargen.setText("Margen");
            } else {
                tTituloDialogMargen.setText("Margen %");
            }
            tTituloDialogMargen.setOnClickListener(view -> {
                esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
                if (esCantidadEnDialogMargen) {
                    tTituloDialogMargen.setText("Margen");
                } else {
                    tTituloDialogMargen.setText("Margen %");
                }
            });
        } else {

            etCantidadDialogMargen.setText(String.format("%.2f", margenC));
            tMargenDialogMargen.setText(String.format("%.2f", margenC));

            if (esCantidadEnDialogMargen) {
                tTituloDialogMargen.setText("MC");
            } else {
                tTituloDialogMargen.setText("MC %");
            }
            tTituloDialogMargen.setOnClickListener(view -> {
                esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
                if (esCantidadEnDialogMargen) {
                    tTituloDialogMargen.setText("MC");
                } else {
                    tTituloDialogMargen.setText("MC %");
                }
            });
        }


        bLimpiarDialogMargen.setOnClickListener(clickListenerDialogMargen);
        bRegresarDialogMargen.setOnClickListener(clickListenerDialogMargen);

        if (!esDialogMargenConversion)
            bSalirDialogMargen.setOnClickListener(clickListenerDialogMargen);
        else
            bSalirDialogMargen.setOnClickListener(clickListenerDialogMargenConversionBotonSalir);
        etCantidadDialogMargen.addTextChangedListener(textWatcherDialogMargen);
        etPorcentajeDialogMargen.addTextChangedListener(textWatcherDialogMargen);
        etPrecisionDialogMargen.addTextChangedListener(textWatcherDialogMargen);
        tTituloCantidadDialogMargen.setOnClickListener(clickListenerDialogMargen);
        tTituloPorcentajeDialogMargen.setOnClickListener(clickListenerDialogMargen);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    TextWatcher textWatcherDialogMargen = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (seAplanoLimpiarDialogMargen &&
                    etCantidadDialogMargen.getText().toString().isEmpty())
                return;

            if (!etCantidadDialogMargen.getText().toString().isEmpty() &&
                    !etCantidadDialogMargen.getText().toString().equals(".") &&
                    !etPorcentajeDialogMargen.getText().toString().isEmpty() &&
                    !etPorcentajeDialogMargen.getText().toString().equals(".")) {

                seAplanoLimpiarDialogMargen = !seAplanoLimpiarDialogMargen;

                if (etPrecisionDialogMargen.getText().toString().isEmpty())
                    formatoDialogMargen = "%,.2f";
                else {
                    formatoDialogMargen = "%,." + etPrecisionDialogMargen.getText().toString() + "f";
                }


                double cantidadDialogMargen, porcentajeDialogMargen;

                cantidadDialogMargen = Double.parseDouble(etCantidadDialogMargen.getText().toString());
                porcentajeDialogMargen = Double.parseDouble(etPorcentajeDialogMargen.getText().toString());
                porcentajeDialogMargen /= 100;
                numDialogMargen = cantidadDialogMargen * porcentajeDialogMargen;

                if (numDialogMargen % 1 > 0)
                    tMargenDialogMargen.setText(String.format(formatoDialogMargen, numDialogMargen));
                else
                    tMargenDialogMargen.setText(String.format("%,.0f", numDialogMargen));

            } else {
                tMargenDialogMargen.setText(etCantidadDialogMargen.getText().toString());
                numDialogMargen = Double.valueOf(etCantidadDialogMargen.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    View.OnClickListener clickListenerDialogMargen = view -> {
        switch (view.getId()) {
            case R.id.b_limpiar_dialog_margen:
                seAplanoLimpiarDialogMargen = true;
                resCantidadDialogMargin = etCantidadDialogMargen.getText().toString();
                resPorcentajeDialogMargin = etPorcentajeDialogMargen.getText().toString();
                etCantidadDialogMargen.getText().clear();
                etPorcentajeDialogMargen.getText().clear();
                tMargenDialogMargen.setText("0.00");
                numDialogMargen = 0;
                break;
            case R.id.b_regresar_dialog_margen:
                etCantidadDialogMargen.setText(resCantidadDialogMargin);
                etPorcentajeDialogMargen.setText(resPorcentajeDialogMargin);
                break;
            case R.id.b_salir_dialog_margen:

                respaldoDeET();
                numDialogMargen = Double.parseDouble(tMargenDialogMargen.
                        getText().toString().replace(",", ""));
                numDialogMargen *= apalancamiento;

                if (esCantidadEnDialogMargen) {

                    numDialogMargen *= porcentajeEntero / 100;

                    if (numDialogMargen % 1 > 0) {
                        etCantidadMostrador.setText(String.format("%.2f", numDialogMargen));
                        etCantidad.setText(String.format("%.4f", numDialogMargen));

                    } else {
                        etCantidadMostrador.setText(String.format("%.0f", numDialogMargen));
                    }

                } else {
                    double porcEntero = cantidad / numDialogMargen;
                    porcEntero *= 100;

                    if (porcEntero % 1 > 0) {
                        etPorcentaje.setText(String.format("%.4f", porcEntero));

                    } else {
                        etPorcentaje.setText(String.format("%.0f", porcEntero));
                    }
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                break;

            case R.id.t_titulo_cantidad_dialog_margen:
                etCantidadDialogMargen.getText().clear();
                break;
            case R.id.t_titulo_porcen_dialog_margen:
                etPorcentajeDialogMargen.getText().clear();
                break;
        }
    };

    View.OnClickListener clickListenerDialogMargenConversionBotonSalir = view -> {
        respaldoDeET();
        numDialogMargen = Double.parseDouble(tMargenDialogMargen.
                getText().toString().replace(",", ""));
        numDialogMargen *= apalancamiento;

        numDialogMargen *= referencia;

        if (esCantidadEnDialogMargen) {

            numDialogMargen *= porcentajeEntero / 100;

            if (numDialogMargen % 1 > 0) {
                etCantidadMostrador.setText(String.format("%.2f", numDialogMargen));
                etCantidad.setText(String.format("%.4f", numDialogMargen));

            } else {
                etCantidadMostrador.setText(String.format("%.0f", numDialogMargen));
            }

        } else {
            double porcEntero = cantidad / numDialogMargen;
            porcEntero *= 100;

            if (porcEntero % 1 > 0) {
                etPorcentaje.setText(String.format("%.4f", porcEntero));

            } else {
                etPorcentaje.setText(String.format("%.0f", porcEntero));
            }
        }

        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        dialog.dismiss();
    };

    EditText etPrecioDialogPos, etPorcentajeDialogPos, etPrecisionDialogPos;
    TextView tPrecioDialogPos, tPorcentajeDialogPos, tSuperiorDialogPos, tInferiorDialogPos;
    double precioDialogPos, porcentajeDialogPos, superiorDialogPos, inferiorDialogPos;
    Button bLimpiarDialogPos, bRegresarDialogPos, bSalirDialogPos;
    String formatoDialgoPos, resPrecioDialogPos, resPorcentajeDialogPos;
    boolean seLimpioDIalogPos, resSeLimpiDialogoPos;

    private void crearDialogPos() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog_pos);
        dialog.show();
        etPrecioDialogPos = dialog.findViewById(R.id.et_precio_dialog_pos);
        etPrecioDialogPos.addTextChangedListener(textWatcherDialogPos);
        etPorcentajeDialogPos = dialog.findViewById(R.id.et_porcentaje_dialog_pos);
        etPorcentajeDialogPos.addTextChangedListener(textWatcherDialogPos);
        tPrecioDialogPos = dialog.findViewById(R.id.t_titulo_precio_dialog_pos);
        tPrecioDialogPos.setOnClickListener(clickListenerDialogPos);
        tPorcentajeDialogPos = dialog.findViewById(R.id.t_titulo_porcen_dialog_pos);
        tPorcentajeDialogPos.setOnClickListener(clickListenerDialogPos);
        tSuperiorDialogPos = dialog.findViewById(R.id.t_superior_dialog_pos);
        tSuperiorDialogPos.setOnClickListener(clickListenerDialogPos);
        tInferiorDialogPos = dialog.findViewById(R.id.t_inferior_dialog_pos);
        tInferiorDialogPos.setOnClickListener(clickListenerDialogPos);
        bLimpiarDialogPos = dialog.findViewById(R.id.b_limpiar_dialog_pos);
        bLimpiarDialogPos.setOnClickListener(clickListenerDialogPos);
        bRegresarDialogPos = dialog.findViewById(R.id.b_regresar_dialog_pos);
        bRegresarDialogPos.setOnClickListener(clickListenerDialogPos);
        bSalirDialogPos = dialog.findViewById(R.id.b_salir_dialog_pos);
        bSalirDialogPos.setOnClickListener(clickListenerDialogPos);
        etPrecisionDialogPos = dialog.findViewById(R.id.et_precision_dialog_pos);
        etPrecisionDialogPos.addTextChangedListener(textWatcherDialogPos);

        if (!seLimpioDIalogPos) {
            etPrecioDialogPos.setText(String.valueOf(precioDialogPos));
            etPorcentajeDialogPos.setText(String.valueOf(porcentajeDialogPos * 100));
        }


        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    TextWatcher textWatcherDialogPos = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!etPrecioDialogPos.getText().toString().isEmpty() &&
                    !etPrecioDialogPos.getText().toString().equals(".") &&
                    !etPorcentajeDialogPos.getText().toString().isEmpty() &&
                    !etPorcentajeDialogPos.getText().toString().equals(".")) {

                if (seLimpioDIalogPos)
                    seLimpioDIalogPos = false;

                precioDialogPos = Double.valueOf(etPrecioDialogPos.getText().toString());
                porcentajeDialogPos = Double.valueOf(etPorcentajeDialogPos.getText().toString());
                porcentajeDialogPos /= 100;

                superiorDialogPos = precioDialogPos * (1 + porcentajeDialogPos);
                inferiorDialogPos = precioDialogPos * (1 - porcentajeDialogPos);

                if (etPrecisionDialogPos.getText().toString().isEmpty())
                    formatoDialgoPos = "%,.5f";
                else {
                    formatoDialgoPos = "%,." + etPrecisionDialogPos.getText().toString() + "f";
                }


                tSuperiorDialogPos.setText(String.format(formatoDialgoPos, superiorDialogPos));
                tInferiorDialogPos.setText(String.format(formatoDialgoPos, inferiorDialogPos));

            } else {
                tSuperiorDialogPos.setText("0.00");
                tInferiorDialogPos.setText("0.00");
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    View.OnClickListener clickListenerDialogPos = view -> {

        switch (view.getId()) {
            case R.id.t_titulo_precio_dialog_pos: {
                etPrecioDialogPos.getText().clear();
                break;
            }
            case R.id.t_titulo_porcen_dialog_pos: {
                etPorcentajeDialogPos.getText().clear();
                break;
            }
            case R.id.b_limpiar_dialog_pos: {
                resPrecioDialogPos = etPrecioDialogPos.getText().toString();
                resPorcentajeDialogPos = etPorcentajeDialogPos.getText().toString();
                etPrecioDialogPos.getText().clear();
                etPorcentajeDialogPos.getText().clear();
                seLimpioDIalogPos = true;
                break;
            }

            case R.id.b_regresar_dialog_pos: {
                etPrecioDialogPos.setText(resPrecioDialogPos);
                etPorcentajeDialogPos.setText(resPrecioDialogPos);
                break;
            }

            case R.id.t_superior_dialog_pos: {
                copyToast(tSuperiorDialogPos.getText().toString()
                        .replace(",", ""));
                break;
            }

            case R.id.t_inferior_dialog_pos: {
                copyToast(tInferiorDialogPos.getText().toString()
                        .replace(",", ""));
                break;
            }

            case R.id.b_salir_dialog_pos: {
                dialog.dismiss();
                break;
            }
        }

    };


    View.OnLongClickListener onLongClickListener = view -> {
        switch (view.getId()) {
            case R.id.b_redondeo_ascendente:
                crearDialogRedondeo(true);
                break;
            case R.id.b_redondeo_descendente:
                crearDialogRedondeo(false);
                break;
            case R.id.b_apalancamiento:
                crearDialogApalancamiento();
                break;

        }
        return true;
    };

    View.OnClickListener clickListenerPosicion = view -> {
        switch (view.getId()) {

            case R.id.t_posicion_tamano:
                if (!tTamano.getText().toString().equals("TP"))
                    crearDialogTamano();
                break;

            case R.id.t_posicion_tamano_conversion:
                if (!tTamanoC.getText().toString().equals("TPC"))
                    crearDialogTamanoConversion();
                break;

            case R.id.t_posicion_lote:
                if (!tTamanoC.getText().toString().equals("TP"))
                    crearDialogLotes();
                break;
            case R.id.t_posicion_lote_conversion:
                if (!tTamanoC.getText().toString().equals("TPC"))
                    crearDialogLotesConversion();
                break;
            case R.id.t_margen:
                if (!tTamano.getText().toString().equals("TP"))
                    crearDialogMargen(false);
                break;
            case R.id.t_margenC:
                if (!tTamanoC.getText().toString().equals("TPC"))
                    crearDialogMargen(true);
                break;
        }
    };

    View.OnClickListener onClickListener = view -> {

        switch (view.getId()) {
            case R.id.b_regresar_claro:

                if (seAplanoLimpiar) {

                    if (resCantidad == null)
                        break;

                    etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(resCantidad)));
                    etCantidad.setText(resCantidad);
                    etPorcentaje.setText(resPorcentaje);
                    etReferencia.setText(resReferencia);
                    redondeoRef = ajusteRefRespaldo;
                    yaRedondeo = resYaRedondeo;
                    precioDialogPos = resPrecioXDialogPos;
                    porcentajeDialogPos = resPorcentajeXDialogPos;
                    seLimpioDIalogPos = resSeLimpiDialogoPos;
                    seAplanoLimpiar = false;
                }
                break;

            case R.id.b_limpiar_claro:
                respaldoDeET();
                etCantidadMostrador.getText().clear();
                etPorcentaje.getText().clear();
                etReferencia.getText().clear();
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
                resPrecioXDialogPos = precioDialogPos;
                resPorcentajeXDialogPos = porcentajeDialogPos;
                resSeLimpiDialogoPos = seLimpioDIalogPos;
                seLimpioDIalogPos = true;
                break;


            case R.id.t_titulo_cant:
                respaldoDeET();
                etCantidadMostrador.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_porcen:
                respaldoDeET();
                etPorcentaje.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_convers:
                respaldoDeET();
                etReferencia.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_tamano:
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

            case R.id.b_xt:
                Intent i = getActivity().getPackageManager().
                        getLaunchIntentForPackage("com.smoowy.xTrade");
                startActivity(i);
                break;

            case R.id.b_pr:
                crearDialogPos();
                break;

            case R.id.b_apalancamiento:

                tipoApalancamiento += 1;

                if (tipoApalancamiento > 4)
                    tipoApalancamiento = 1;

                cambioApalancamiento();
                break;


            case R.id.b_redondeo_descendente:
                ajusteRedondeo(false);
                break;


            case R.id.b_redondeo_ascendente:
                ajusteRedondeo(true);
                break;
        }

    };

    private void respaldoDeET() {
        seAplanoLimpiar = true;
        resCantidad = etCantidad.getText().toString();
        resCantidadMostrador = etCantidadMostrador.getText().toString();
        resPorcentaje = etPorcentaje.getText().toString();
        resReferencia = etReferencia.getText().toString();
        resYaRedondeo = yaRedondeo;
        ajusteRefRespaldo = redondeoRef;
    }

    private void ajusteRedondeo(boolean esAscendente) {
        double restante, restanteFinal, num,
                tamanoPosicioncAjustada, tamanoPosicionAjustada;

        respaldoDeET();

        if (!tTamanoC.getText().toString().equals("TPC")) {

            if (!yaRedondeo) {

                if (hayDecimales)
                    tamanoPosicioncAjustada = tamanoPosicionC;
                else
                    tamanoPosicioncAjustada = Math.round(tamanoPosicionC);


                restante = tamanoPosicioncAjustada % redondeoRef;
                restanteFinal = redondeoRef - restante;

                if (esAscendente)
                    num = tamanoPosicioncAjustada + restanteFinal;
                else
                    num = tamanoPosicioncAjustada - restante;

            } else {

                tamanoPosicioncAjustada = tamanoPosicionC;


                if (esAscendente)
                    num = tamanoPosicioncAjustada + redondeoRef;

                else {
                    num = tamanoPosicioncAjustada - redondeoRef;
                }
            }

            num *= referencia;
            num *= (porcentajeEntero / 100);

            if (num % 1 > 0) {
                etCantidadMostrador.setText(String.format("%.2f", num));
                etCantidad.setText(String.format("%.4f", num));

            } else {
                etCantidadMostrador.setText(String.format("%.0f", num));

            }


        } else if (!tTamano.getText().toString().equals("TP")) {


            if (!yaRedondeo) {

                if (hayDecimales)
                    tamanoPosicioncAjustada = tamanoPosicion;
                else
                    tamanoPosicioncAjustada = Math.round(tamanoPosicion);


                restante = tamanoPosicioncAjustada % redondeoRef;
                restanteFinal = redondeoRef - restante;

                if (esAscendente)
                    num = tamanoPosicioncAjustada + restanteFinal;
                else
                    num = tamanoPosicioncAjustada - restante;

            } else {

                tamanoPosicioncAjustada = tamanoPosicion;


                if (esAscendente)
                    num = tamanoPosicioncAjustada + redondeoRef;

                else {
                    num = tamanoPosicioncAjustada - redondeoRef;
                }
            }


            num *= (porcentajeEntero / 100);

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
                porcentajeEntero = Double.parseDouble(etPorcentaje.getText().toString());
                tamanoPosicion = cantidad / (porcentajeEntero / 100);


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

    @Override
    public void onDestroy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tipoApalancamiento", tipoApalancamiento);
        editor.putBoolean("hayDecimales", hayDecimales);
        editor.putString("cantidad", etCantidad.getText().toString());
        editor.putString("porcentajeEntero", etPorcentaje.getText().toString());
        editor.putString("referencia", etReferencia.getText().toString());
        editor.putString("redondeoRef", String.valueOf(redondeoRef));
        editor.putInt("apalancamiento", apalancamiento);
        editor.putString("precioDialogPos", String.valueOf(precioDialogPos));
        editor.putString("porcentajeDialogPos", String.valueOf(porcentajeDialogPos));
        editor.putBoolean("seLimpioDIalogPos", seLimpioDIalogPos);
        editor.apply();
        super.onDestroy();
    }

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

        if (sharedPreferences.contains("porcentajeEntero"))
            etPorcentaje.setText(sharedPreferences.getString("porcentajeEntero", ""));
        if (sharedPreferences.contains("referencia"))
            etReferencia.setText(sharedPreferences.getString("referencia", ""));
        if (sharedPreferences.contains("redondeoRef"))
            redondeoRef = Double.parseDouble(sharedPreferences.getString("redondeoRef", "1000"));
        if (sharedPreferences.contains("apalancamiento"))
            apalancamiento = sharedPreferences.getInt("apalancamiento", 100);
        if (sharedPreferences.contains("precioDialogPos"))
            precioDialogPos = Double.valueOf(sharedPreferences.getString("precioDialogPos", "0"));
        if (sharedPreferences.contains("porcentajeDialogPos"))
            porcentajeDialogPos = Double.valueOf(sharedPreferences.getString("porcentajeDialogPos", "0"));
        if (sharedPreferences.contains("seLimpioDIalogPos"))
            seLimpioDIalogPos = sharedPreferences.getBoolean("seLimpioDIalogPos", false);
    }


    private void copyToast(CharSequence text) {

        ClipData clip = ClipData.newPlainText("Precio", text.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Precio grabada: " + text.toString(), Toast.LENGTH_SHORT).show();
    }


}
