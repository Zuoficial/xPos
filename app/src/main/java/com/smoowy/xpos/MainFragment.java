package com.smoowy.xpos;


import android.app.Dialog;

import androidx.lifecycle.Lifecycle;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainFragment extends Fragment {


    public MainFragment() {
    }

    EditText etCantidad, etPorcentaje, etReferencia, etCantidadMostrador, etPrecision;
    TextView tTamano, tLote, tTamanoC, tLoteC,
            tTituloCantidad, tTituloPorcentaje, tTituloReferencia, tTituloTamano, tMargen, tMargenC,
            tSeguro, tSeguroC, tMantener, tCantidadMantener;
    double cantidad, porcentajeEntero, referencia, tamanoPosicion,
            lote, tamanoPosicionC, loteC, margen, margenC, necesario, necesarioC,
            redondeoRef = 1000, ajusteRefRespaldo, resPrecioXDialogPos,
            resPorcentajeXDialogPos,porcentajeMantenerGuardado;
    String resCantidadDialogReferencia, resPrecioDialogReferencia;
    int apalancamiento;
    int tipoApalancamiento = 4;
    int idOperacion;
    boolean hayDecimales, yaRedondeo, resYaRedondeo, seAplanoLimpiar,
            resHayDatosDialogReferencia, resHayDatosDialogCantidad, hayDecimalesDoble, seAplanoMantener;
    ClipboardManager clipboard;
    Button bApalancamiento, bLimpiarClaro, bRedondeoDescendente,
            bRedondeoAscendente, bRegresarClaro, bPR, bXT, bId, bIdMenos, bIdMas, bBorrarTodo;
    SharedPreferences sharedPreferences;
    String resCantidad, resCantidadMostrador, resPorcentaje, resReferencia, precision = "", resPrecision;
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
        etPrecision = view.findViewById(R.id.et_precision);
        etCantidad.addTextChangedListener(textWatcher);
        etPorcentaje.addTextChangedListener(textWatcher);
        etReferencia.addTextChangedListener(textWatcher);
        etPrecision.addTextChangedListener(textWatcherPrecision);
        tMantener = view.findViewById(R.id.t_mantener);
        tMantener.setOnClickListener(onClickListener);
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
        tTituloReferencia = view.findViewById(R.id.t_titulo_referencia);
        tTituloTamano = view.findViewById(R.id.t_titulo_tamano);
        tTituloCantidad.setOnClickListener(onClickListener);
        tTituloCantidad.setOnLongClickListener(onLongClickListener);
        tTituloPorcentaje.setOnClickListener(onClickListener);
        tTituloReferencia.setOnClickListener(onClickListener);
        tTituloReferencia.setOnLongClickListener(onLongClickListener);
        tTituloPorcentaje.setOnLongClickListener(onLongClickListener);
        tTituloTamano.setOnClickListener(onClickListener);
        tTituloTamano.setOnLongClickListener(onLongClickListener);
        tSeguro = view.findViewById(R.id.t_seguro);
        tSeguroC = view.findViewById(R.id.t_seguroC);
        tMargen = view.findViewById(R.id.t_margen);
        tMargen.setOnClickListener(clickListenerPosicion);
        tMargenC = view.findViewById(R.id.t_margenC);
        tMargenC.setOnClickListener(clickListenerPosicion);
        tCantidadMantener = view.findViewById(R.id.t_cantidad_mostrador_mantener);
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
        bId = view.findViewById(R.id.b_id);
        bIdMenos = view.findViewById(R.id.b_id_menos);
        bIdMas = view.findViewById(R.id.b_id_mas);
        bIdMenos.setOnClickListener(onClickListener);
        bIdMas.setOnClickListener(onClickListener);
        bBorrarTodo = view.findViewById(R.id.b_borrarTodo);
        bBorrarTodo.setOnClickListener(onClickListener);
        recuperarIdShared();
        accederDB();
        cambioApalancamiento();
        bId.setText(String.valueOf(idOperacion));
        ponerKeyListener(etCantidadMostrador);
        ponerKeyListener(etPorcentaje);
        ponerKeyListener(etReferencia);
        return view;
    }

    void ponerKeyListener(EditText editText) {
        editText.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_NUMPAD_ADD) {
                editText.setText(editText.getText() + "000");
                editText.setSelection(editText.getText().length());
                return true;
            } else
                return false;
        });
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

        etDialogReferencia.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_NUMPAD_ADD) {
                etDialogReferencia.setText(etDialogReferencia.getText() + "000");
                etDialogReferencia.setSelection(etDialogReferencia.getText().length());
            }


            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                if (!etDialogReferencia.getText().toString().isEmpty() &&
                        !etDialogReferencia.getText().toString().equals(".")) {
                    redondeoRef = Double.parseDouble(etDialogReferencia.getText().toString());
                    if (esAscendente)
                        ajusteRedondeo(true);
                    else
                        ajusteRedondeo(false);
                }
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
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                if (!etDialogReferencia.getText().toString().isEmpty() &&
                        !etDialogReferencia.getText().toString().equals(".")) {
                    apalancamiento = (int) Double.parseDouble(etDialogReferencia.getText().toString());
                    tipoApalancamiento = 0;
                    cambioApalancamiento();
                }
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });

        etDialogReferencia.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    TextView tTituloDialogTamano, tCantidadTituloDialogTamano, tValorTituloDialogTamano,
            tValorDialogTamano;
    EditText etDialogTamano, etCantidadDialogTamano;
    Button bSalirDialogTamano, bLimpiarDialogTamano;

    private void crearDialogTamano() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog_margen);
        dialog.show();

        dialog.findViewById(R.id.et_precision_dialog_margen).setVisibility(View.GONE);
        tTituloDialogTamano = dialog.findViewById(R.id.t_titulo_cantidad_dialog_margen);
        etDialogTamano = dialog.findViewById(R.id.et_cantidad_dialog_margen);
        etDialogTamano.addTextChangedListener(textWatcherDialogTamano);
        tCantidadTituloDialogTamano = dialog.findViewById(R.id.t_titulo_porcen_dialog_margen);
        tCantidadTituloDialogTamano.setText("Cantidad");
        etCantidadDialogTamano = dialog.findViewById(R.id.et_porcentaje_dialog_margen);
        etCantidadDialogTamano.setHint("Cantidad");
        etCantidadDialogTamano.addTextChangedListener(textWatcherDialogTamano);
        tValorTituloDialogTamano = dialog.findViewById(R.id.t_titulo_margen_dialog_margen);
        tValorTituloDialogTamano.setText("Valor");
        tValorDialogTamano = dialog.findViewById(R.id.t_margen_dialog_margen);
        View.OnClickListener clicklistenerDialogTamano = view -> {


            switch (view.getId()) {

                case R.id.b_salir_dialog_margen:
                    if (!etDialogTamano.getText().toString().isEmpty() &&
                            !etDialogTamano.getText().toString().equals(".")) {
                        respaldoDeET();

                        if (esCantidadEnDialogMargen) {
                            double num = Double.parseDouble(tValorDialogTamano.getText().toString().replace(",", ""));
                            num *= porcentajeEntero / 100;

                            if (num % 1 > 0) {
                                etCantidadMostrador.setText(String.format("%.2f", num));
                                etCantidad.setText(String.format("%.4f", num));

                            } else {
                                etCantidadMostrador.setText(String.format("%.0f", num));
                            }
                        } else {
                            double num = Double.parseDouble(tValorDialogTamano.getText().toString().replace(",", ""));
                            double numRef = Double.parseDouble(etCantidad.getText().toString());
                            double porcen = (numRef / num) * 100;

                            if (porcen % 1 > 0) {
                                etPorcentaje.setText(String.format("%.4f", porcen));

                            } else {
                                etPorcentaje.setText(String.format("%.0f", porcen));
                            }
                        }

                        yaRedondeo = false;
                    }

                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.dismiss();
                    break;

                case R.id.b_limpiar_dialog_margen:
                    etDialogTamano.setText("");
                    etCantidadDialogTamano.setText("");
                    tValorDialogTamano.setText("0.00");
                    break;
            }


        };
        bSalirDialogTamano = dialog.findViewById(R.id.b_salir_dialog_margen);
        bSalirDialogTamano.setOnClickListener(clicklistenerDialogTamano);
        bLimpiarDialogTamano = dialog.findViewById(R.id.b_limpiar_dialog_margen);
        bLimpiarDialogTamano.setOnClickListener(clicklistenerDialogTamano);

        if (hayDecimalesDoble) {
            etDialogTamano.setText(String.format("%.2f", tamanoPosicion));
            tValorDialogTamano.setText(String.format("%,.2f", tamanoPosicion));
        } else {
            etDialogTamano.setText(String.format("%.0f", tamanoPosicion));
            tValorDialogTamano.setText(String.format("%,.0f", tamanoPosicion));
        }


        if (esCantidadEnDialogMargen) {
            tTituloDialogTamano.setText("TP");
        } else {
            tTituloDialogTamano.setText("TP %");
        }

        tTituloDialogTamano.setOnClickListener(view -> {
            esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
            if (esCantidadEnDialogMargen) {
                tTituloDialogTamano.setText("TP");
            } else {
                tTituloDialogTamano.setText("TP %");
            }
        });
        tTituloDialogTamano.setOnLongClickListener(view -> {
            etDialogTamano.getText().clear();
            return true;
        });

        etDialogTamano.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogTamano.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_NUMPAD_ADD) {
                etDialogTamano.setText(etDialogTamano.getText() + "000");
                etDialogTamano.setSelection(etDialogTamano.getText().length());
            }

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                bSalirDialogTamano.performClick();
                return true;

            } else
                return false;
        });


    }

    TextWatcher textWatcherDialogTamano = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!etDialogTamano.getText().toString().isEmpty() &&
                    !etCantidadDialogTamano.getText().toString().isEmpty()) {


                double referencia = Double.parseDouble(etDialogTamano.getText().toString());
                double cantidad = Double.parseDouble(etCantidadDialogTamano.getText().toString());

                if (hayDecimalesDoble) {
                    tValorDialogTamano.setText(String.format("%,.2f", (referencia * cantidad)));
                } else {
                    tValorDialogTamano.setText(String.format("%,.0f", (referencia * cantidad)));
                }
            } else if (!etDialogTamano.getText().toString().isEmpty() &&
                    etCantidadDialogTamano.getText().toString().isEmpty()) {

                if (hayDecimalesDoble) {
                    tValorDialogTamano.setText(String.format("%,.2f", (Double.parseDouble(etDialogTamano.getText().toString()))));
                } else {
                    tValorDialogTamano.setText(String.format("%,.0f", (Double.parseDouble(etDialogTamano.getText().toString()))));
                }
            }


        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


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
        if (esCantidadEnDialogMargen) {
            tDialogTitulo.setText("TPC");
        } else {
            tDialogTitulo.setText("TPC %");
        }

        tDialogTitulo.setOnClickListener(view -> {
            esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
            if (esCantidadEnDialogMargen) {
                tDialogTitulo.setText("TPC");
            } else {
                tDialogTitulo.setText("TPC %");
            }
        });
        tDialogTitulo.setOnLongClickListener(view -> {
            etDialogReferencia.getText().clear();
            return true;
        });

        etDialogReferencia.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_NUMPAD_ADD) {
                etDialogReferencia.setText(etDialogReferencia.getText() + "000");
                etDialogReferencia.setSelection(etDialogReferencia.getText().length());
            }

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                if (!etDialogReferencia.getText().toString().isEmpty() &&
                        !etDialogReferencia.getText().toString().equals(".")) {
                    respaldoDeET();

                    if (esCantidadEnDialogMargen) {
                        double num = Double.parseDouble(etDialogReferencia.getText().toString());
                        num *= referencia;
                        num *= porcentajeEntero / 100;

                        if (num % 1 > 0) {
                            etCantidadMostrador.setText(String.format("%.2f", num));
                            etCantidad.setText(String.format("%.4f", num));

                        } else {
                            etCantidadMostrador.setText(String.format("%.0f", num));
                        }
                    } else {
                        double num = Double.parseDouble(etDialogReferencia.getText().toString());
                        num *= referencia;
                        double numRef = Double.parseDouble(etCantidad.getText().toString());
                        double porcen = (numRef / num) * 100;

                        if (porcen % 1 > 0) {
                            etPorcentaje.setText(String.format("%.4f", porcen));

                        } else {
                            etPorcentaje.setText(String.format("%.0f", porcen));
                        }
                    }


                    yaRedondeo = false;
                }
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }

    TextWatcher textWatcherPrecision = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            if (!tTamanoC.getText().toString().equals("TPC")) {

                if (!hayDecimales) {
                    tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                } else {

                    if (etPrecision.getText().toString().isEmpty())
                        tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                    else {

                        tTamanoC.setText(String.format("%,." +
                                (precision = etPrecision.getText().toString()) +
                                "f", tamanoPosicionC));
                    }
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void crearDialogLotes() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.4f", tamanoPosicion / 100000));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        if (esCantidadEnDialogMargen) {
            tDialogTitulo.setText("Lotes");
        } else {
            tDialogTitulo.setText("Lotes %");
        }

        tDialogTitulo.setOnClickListener(view -> {
            esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
            if (esCantidadEnDialogMargen) {
                tDialogTitulo.setText("Lotes");
            } else {
                tDialogTitulo.setText("Lotes %");
            }
        });
        tDialogTitulo.setOnLongClickListener(view -> {
            etDialogReferencia.getText().clear();
            return true;
        });
        etDialogReferencia.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                if (!etDialogReferencia.getText().toString().isEmpty() &&
                        !etDialogReferencia.getText().toString().equals(".")) {
                    respaldoDeET();

                    if (esCantidadEnDialogMargen) {
                        double num = Double.parseDouble(etDialogReferencia.getText().toString());
                        num *= 100000;
                        num *= porcentajeEntero / 100;

                        if (num % 1 > 0) {
                            etCantidadMostrador.setText(String.format("%.2f", num));
                            etCantidad.setText(String.format("%.4f", num));

                        } else {
                            etCantidadMostrador.setText(String.format("%.0f", num));
                        }
                    } else {
                        double num = Double.parseDouble(etDialogReferencia.getText().toString());
                        num *= 100000;
                        double numRef = Double.parseDouble(etCantidad.getText().toString());
                        double porcen = (numRef / num) * 100;

                        if (porcen % 1 > 0) {
                            etPorcentaje.setText(String.format("%.4f", porcen));

                        } else {
                            etPorcentaje.setText(String.format("%.0f", porcen));
                        }

                    }

                    yaRedondeo = false;
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
        if (esCantidadEnDialogMargen) {
            tDialogTitulo.setText("LC");
        } else {
            tDialogTitulo.setText("LC %");
        }

        tDialogTitulo.setOnClickListener(view -> {
            esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
            if (esCantidadEnDialogMargen) {
                tDialogTitulo.setText("LC");
            } else {
                tDialogTitulo.setText("LC %");
            }
        });
        tDialogTitulo.setOnLongClickListener(view -> {
            etDialogReferencia.getText().clear();
            return true;
        });
        etDialogReferencia.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                if (!etDialogReferencia.getText().toString().isEmpty() &&
                        !etDialogReferencia.getText().toString().equals(".")) {
                    respaldoDeET();
                    if (esCantidadEnDialogMargen) {
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
                    } else {
                        double num = Double.parseDouble(etDialogReferencia.getText().toString());
                        num *= 100000;
                        num *= referencia;
                        double numRef = Double.parseDouble(etCantidad.getText().toString());
                        double porcen = (numRef / num) * 100;

                        if (porcen % 1 > 0) {
                            etPorcentaje.setText(String.format("%.4f", porcen));

                        } else {
                            etPorcentaje.setText(String.format("%.0f", porcen));
                        }

                    }

                    yaRedondeo = false;
                }
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    boolean esCantidadEnDialogMargen = true;
    boolean esDialogReferenciaBoolean;
    EditText etCantidadDialogMargen, etPorcentajeDialogMargen, etPrecisionDialogMargen;
    TextView tMargenDialogMargen, tTituloDialogMargen,
            tTituloCantidadDialogMargen, tTituloPorcentajeDialogMargen;
    Button bLimpiarDialogMargen, bRegresarDialogMargen, bSalirDialogMargen;
    String resCantidadDialogMargin, resPorcentajeDialogMargin, formatoDialogMargen = "%,.2f";
    double numDialogMargen;
    boolean seAplanoLimpiarDialogMargen;
    final int esDialogCantidad = 0;
    final int esDialogMargen = 1;
    final int esDialogMargenConversion = 2;
    final int esDialogReferencia = 3;

    private void crearDialogPrecioPorcentaje(int tipoDialog) {
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

        etCantidadDialogMargen.setOnKeyListener((view1, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_NUMPAD_ADD) {
                etCantidadDialogMargen.setText(etCantidadDialogMargen.getText() + "000");
                etCantidadDialogMargen.setSelection(etCantidadDialogMargen.getText().length());
                return true;

            } else
                return false;
        });
        etPorcentajeDialogMargen.setOnKeyListener((view1, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_NUMPAD_ADD) {
                etPorcentajeDialogMargen.setText(etPorcentajeDialogMargen.getText() + "000");
                etPorcentajeDialogMargen.setSelection(etPorcentajeDialogMargen.getText().length());
                return true;

            } else
                return false;
        });

        if (tipoDialog == esDialogCantidad) {

            if (!etCantidad.getText().toString().isEmpty()) {

                double cantidad = Double.parseDouble(etCantidad.getText().toString());

                etCantidadDialogMargen.setText(String.format("%.2f", cantidad));
                tMargenDialogMargen.setText(String.format("%.2f", cantidad));
            } else {

                etCantidadDialogMargen.setText(String.format("%.2f", 0.0));
                tMargenDialogMargen.setText(String.format("%.2f", 0.0));
            }
            tTituloDialogMargen.setText("Cantidad");


        } else if (tipoDialog == esDialogMargen) {

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
        } else if (tipoDialog == esDialogMargenConversion) {

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
        } else if (tipoDialog == esDialogReferencia) {

            if (!etReferencia.getText().toString().isEmpty()) {

                if (hayDatosDialogReferencia) {

                    etCantidadDialogMargen.setText(precioDialogReferencia);
                    tMargenDialogMargen.setText(cantidadDialogReferencia);

                } else {

                    double cantidad = Double.parseDouble(etReferencia.getText().toString());

                    etCantidadDialogMargen.setText(String.format("%.2f", cantidad));
                    tMargenDialogMargen.setText(String.format("%.2f", cantidad));
                }
            } else {


                if (precioDialogReferencia != null && !precioDialogReferencia.equals("")) {
                    etCantidadDialogMargen.setText(precioDialogReferencia);
                    tMargenDialogMargen.setText(cantidadDialogReferencia);

                } else {
                    etCantidadDialogMargen.setText(String.format("%.2f", 0.0));
                    tMargenDialogMargen.setText(String.format("%.2f", 0.0));
                }
            }

            tTituloDialogMargen.setText("Valor");
            tTituloPorcentajeDialogMargen.setText("Cantidad");
            tTituloCantidadDialogMargen.setText("Precio");
            etPorcentajeDialogMargen.setHint("Cantidad");
        }

        esDialogReferenciaBoolean = tipoDialog == esDialogReferencia;
        bLimpiarDialogMargen.setOnClickListener(clickListenerDialogMargen);
        bRegresarDialogMargen.setOnClickListener(clickListenerDialogMargen);
        etCantidadDialogMargen.addTextChangedListener(textWatcherDialogPrecioPorcentaje);
        etPorcentajeDialogMargen.addTextChangedListener(textWatcherDialogPrecioPorcentaje);
        etPrecisionDialogMargen.addTextChangedListener(textWatcherDialogPrecioPorcentaje);
        tTituloCantidadDialogMargen.setOnClickListener(clickListenerDialogMargen);
        tTituloPorcentajeDialogMargen.setOnClickListener(clickListenerDialogMargen);
        if (tipoDialog == esDialogCantidad) {
            bSalirDialogMargen.setOnClickListener(clickListenerDialogCantidadBotonSalir);
            if (hayDatosDialogCantidad) {
                etCantidadDialogMargen.setText(cantidadDialogCantidad);
                etPorcentajeDialogMargen.setText(porcentajeDialogCantidad);
            }

        } else if (tipoDialog == esDialogMargen)
            bSalirDialogMargen.setOnClickListener(clickListenerDialogMargen);
        else if (tipoDialog == esDialogMargenConversion)
            bSalirDialogMargen.setOnClickListener(clickListenerDialogMargenConversionBotonSalir);
        else if (tipoDialog == esDialogReferencia) {
            bSalirDialogMargen.setOnClickListener(clickListenerDialogReferenciaBotonSalir);
            if (hayDatosDialogReferencia) {
                etCantidadDialogMargen.setText(precioDialogReferencia);
                etPorcentajeDialogMargen.setText(cantidadDialogReferencia);
            }
        }
        etCantidadDialogMargen.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    TextWatcher textWatcherDialogPrecioPorcentaje = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (seAplanoLimpiarDialogMargen &&
                    etCantidadDialogMargen.getText().toString().isEmpty()) {
                tMargenDialogMargen.setText("0.00");
                return;
            }

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
                if (!esDialogReferenciaBoolean)
                    porcentajeDialogMargen /= 100;
                numDialogMargen = cantidadDialogMargen * porcentajeDialogMargen;

                if (numDialogMargen % 1 > 0)
                    tMargenDialogMargen.setText(String.format(formatoDialogMargen, numDialogMargen));
                else
                    tMargenDialogMargen.setText(String.format("%,.0f", numDialogMargen));

            } else {

                if (etCantidadDialogMargen.getText().toString().equals(".") ||
                        etCantidadDialogMargen.getText().toString().isEmpty()) {
                    tMargenDialogMargen.setText("");
                    numDialogMargen = 0;
                } else {
                    if (etPrecisionDialogMargen.getText().toString().isEmpty())
                        formatoDialogMargen = "%,.2f";
                    else {
                        formatoDialogMargen = "%,." + etPrecisionDialogMargen.getText().toString() + "f";
                    }
                    double datoCantidadMargen = Double.parseDouble(etCantidadDialogMargen.getText().toString());
                    tMargenDialogMargen.setText(String.format(formatoDialogMargen, datoCantidadMargen));
                    numDialogMargen = Double.valueOf(etCantidadDialogMargen.getText().toString());
                }
            }

            if (etCantidadDialogMargen.getText().toString().isEmpty())
                tMargenDialogMargen.setText("0.00");

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

                if (!etCantidadDialogMargen.getText().toString().isEmpty() &&
                        !etCantidadDialogMargen.getText().toString().equals(".")) {
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
                    yaRedondeo = false;
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
        if (!etCantidadDialogMargen.getText().toString().isEmpty() &&
                !etCantidadDialogMargen.getText().toString().equals(".")) {
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
            yaRedondeo = false;
        }
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        dialog.dismiss();
    };

    String cantidadDialogCantidad, porcentajeDialogCantidad;
    boolean hayDatosDialogCantidad;
    View.OnClickListener clickListenerDialogCantidadBotonSalir = view -> {
        if (!etCantidadDialogMargen.getText().toString().isEmpty() &&
                !etCantidadDialogMargen.getText().toString().equals(".")) {
            respaldoDeET();
            numDialogMargen = Double.parseDouble(tMargenDialogMargen.
                    getText().toString().replace(",", ""));

            if (numDialogMargen % 1 > 0) {

                if (etPrecisionDialogMargen.getText().toString().isEmpty()) {
                    etCantidadMostrador.setText(String.format("%.2f", numDialogMargen));
                    etCantidad.setText(String.format("%.4f", numDialogMargen));

                } else {
                    etCantidadMostrador.setText(String.format("%." + etPrecisionDialogMargen.getText().toString()
                            + "f", numDialogMargen));
                }

            } else {
                etCantidadMostrador.setText(String.format("%.0f", numDialogMargen));
            }

            yaRedondeo = false;

            if (!etCantidadDialogMargen.getText().toString().isEmpty())
                cantidadDialogCantidad = etCantidadDialogMargen.getText().toString();
            else
                cantidadDialogCantidad = "";

            if (!etPorcentajeDialogMargen.getText().toString().isEmpty())
                porcentajeDialogCantidad = etPorcentajeDialogMargen.getText().toString();
            else
                porcentajeDialogCantidad = "";

            hayDatosDialogCantidad = !cantidadDialogCantidad.isEmpty();


        }
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        dialog.dismiss();
    };


    String precioDialogReferencia, cantidadDialogReferencia;
    double valorDialogReferencia;
    boolean hayDatosDialogReferencia;
    View.OnClickListener clickListenerDialogReferenciaBotonSalir = view -> {

        if (!etCantidadDialogMargen.getText().toString().isEmpty() &&
                !etCantidadDialogMargen.getText().toString().equals(".")) {
            respaldoDeET();
            valorDialogReferencia = Double.parseDouble(tMargenDialogMargen.
                    getText().toString().replace(",", ""));

            /*if (valorDialogReferencia % 1 > 0) {

                if (etPrecisionDialogMargen.getText().toString().isEmpty())
                    etReferencia.setText(String.format("%.2f", valorDialogReferencia));
                else
                    etReferencia.setText(String.format("%." + etPrecisionDialogMargen.getText().toString()
                            + "f", valorDialogReferencia));

            } else {
                etReferencia.setText(String.format("%.0f", valorDialogReferencia));
            }*/

            yaRedondeo = false;

            if (!etCantidadDialogMargen.getText().toString().isEmpty())
                precioDialogReferencia = etCantidadDialogMargen.getText().toString();
            else
                precioDialogReferencia = "";

            if (!etPorcentajeDialogMargen.getText().toString().isEmpty())
                cantidadDialogReferencia = etPorcentajeDialogMargen.getText().toString();
            else
                cantidadDialogReferencia = "";

            hayDatosDialogReferencia = !precioDialogReferencia.isEmpty();

            checarTituloReferencia();
            etReferencia.setText(etCantidadDialogMargen.getText().toString());
        }
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        dialog.dismiss();
    };

    EditText etPrecioDialogPos, etPorcentajeDialogPos, etPrecisionDialogPos;
    TextView tPrecioDialogPos, tPorcentajeDialogPos, tSuperiorDialogPos, tInferiorDialogPos;
    double precioDialogPos, porcentajeDialogPos, superiorDialogPos, inferiorDialogPos;
    Button bLimpiarDialogPos, bRegresarDialogPos, bSalirDialogPos;
    String formatoDialogPos, resPrecioDialogPos, resPorcentajeDialogPos;
    boolean seLimpioDialogPos, resSeLimpiDialogoPos;

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

        if (!seLimpioDialogPos) {
            etPrecioDialogPos.setText(String.valueOf(precioDialogPos));
            etPorcentajeDialogPos.setText(String.valueOf(porcentajeDialogPos * 100));
        }

        etPrecioDialogPos.requestFocus();
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

                if (seLimpioDialogPos)
                    seLimpioDialogPos = false;

                precioDialogPos = Double.valueOf(etPrecioDialogPos.getText().toString());
                porcentajeDialogPos = Double.valueOf(etPorcentajeDialogPos.getText().toString());
                porcentajeDialogPos /= 100;

                superiorDialogPos = precioDialogPos * (1 + porcentajeDialogPos);
                inferiorDialogPos = precioDialogPos * (1 - porcentajeDialogPos);

                if (etPrecisionDialogPos.getText().toString().isEmpty())
                    formatoDialogPos = "%,.5f";
                else {
                    formatoDialogPos = "%,." + etPrecisionDialogPos.getText().toString() + "f";
                }


                tSuperiorDialogPos.setText(String.format(formatoDialogPos, superiorDialogPos));
                tInferiorDialogPos.setText(String.format(formatoDialogPos, inferiorDialogPos));

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
                seLimpioDialogPos = true;
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


    //TODO ajustando nueva funcion
    EditText etPrecioDialogPorcentaje, etPrecisionDialogPorcentaje, etLargaDialogPorcentaje, etCortaDialogPorcentaje;
    TextView tPrecioDialogPorcentaje, tPorcentajeDialogPorcentaje, tTituloLargaDialogPorcentaje, tTituloCortaDialogPorcentaje;
    double precioDialogPorcentaje, porcentajeDialogPorcentaje, largaDialogPorcentaje, cortaDialogPorcentaje;
    Button bLimpiarDialogPorcentaje, bRegresarDialogPorcentaje, bSalirDialogPorcentaje;
    String formatoDialogPorcentaje, resPrecioDialogPorcentaje, resPorcentajeDialogPorcentaje;
    boolean seLimpioDialogPorcentaje, resSeLimpiDialogoPorcentaje;

    private void crearDialogPorcentaje() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog_porcentaje);
        dialog.show();
        etPrecioDialogPorcentaje = dialog.findViewById(R.id.et_precio_dialog_porcentaje);
        etPrecioDialogPorcentaje.addTextChangedListener(textWatcherDialogPorcentaje);
        tPorcentajeDialogPorcentaje = dialog.findViewById(R.id.t_porcentaje_dialog_porcentaje);
        tPrecioDialogPorcentaje = dialog.findViewById(R.id.t_titulo_precio_dialog_porcentaje);
        tPrecioDialogPorcentaje.setOnClickListener(clickListenerDialogPorcentaje);
        tTituloLargaDialogPorcentaje = dialog.findViewById(R.id.t_titulo_larga_dialog_porcentaje);
        tTituloLargaDialogPorcentaje.setOnClickListener(clickListenerDialogPorcentaje);
        etLargaDialogPorcentaje = dialog.findViewById(R.id.et_larga_dialog_porcentaje);
        etLargaDialogPorcentaje.addTextChangedListener(textWatcherDialogPorcentaje);
        tTituloCortaDialogPorcentaje = dialog.findViewById(R.id.t_titulo_corta_dialog_porcentaje);
        tTituloCortaDialogPorcentaje.setOnClickListener(clickListenerDialogPorcentaje);
        etCortaDialogPorcentaje = dialog.findViewById(R.id.et_corta_dialog_porcentaje);
        etCortaDialogPorcentaje.addTextChangedListener(textWatcherDialogPorcentaje);
        bLimpiarDialogPorcentaje = dialog.findViewById(R.id.b_limpiar_dialog_porcentaje);
        bLimpiarDialogPorcentaje.setOnClickListener(clickListenerDialogPorcentaje);
        bRegresarDialogPorcentaje = dialog.findViewById(R.id.b_regresar_dialog_porcentaje);
        bRegresarDialogPorcentaje.setOnClickListener(clickListenerDialogPorcentaje);
        bSalirDialogPorcentaje = dialog.findViewById(R.id.b_salir_dialog_porcentaje);
        bSalirDialogPorcentaje.setOnClickListener(clickListenerDialogPorcentaje);
        etPrecisionDialogPorcentaje = dialog.findViewById(R.id.et_precision_dialog_porcentaje);
        //etPrecisionDialogPorcentaje.addTextChangedListener(textWatcherDialogPorcentaje);
        etCortaDialogPorcentaje.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                bSalirDialogPorcentaje.requestFocus();
                return true;
            } else
                return false;
        });

        if (!seLimpioDialogPorcentaje) {
            etPrecioDialogPorcentaje.setText(String.valueOf(precioDialogPorcentaje));
            if (cortaDialogPorcentaje != 0)
                etCortaDialogPorcentaje.setText(String.valueOf(cortaDialogPorcentaje));
            if (largaDialogPorcentaje != 0)
                etLargaDialogPorcentaje.setText(String.valueOf(largaDialogPorcentaje));
        }


        etPrecioDialogPorcentaje.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


    }

    View.OnClickListener clickListenerDialogPorcentaje = view -> {

        switch (view.getId()) {
            case R.id.t_titulo_precio_dialog_porcentaje:
                etPrecioDialogPorcentaje.getText().clear();
                tPorcentajeDialogPorcentaje.setText("Porcentaje");
                etPrecioDialogPorcentaje.requestFocus();
                break;
            case R.id.t_titulo_larga_dialog_porcentaje:
                etLargaDialogPorcentaje.getText().clear();
                tPorcentajeDialogPorcentaje.setText("Porcentaje");
                etLargaDialogPorcentaje.requestFocus();

                break;
            case R.id.t_titulo_corta_dialog_porcentaje:
                etCortaDialogPorcentaje.getText().clear();
                tPorcentajeDialogPorcentaje.setText("Porcentaje");
                etCortaDialogPorcentaje.requestFocus();
                break;
            case R.id.b_salir_dialog_porcentaje:
                if (!seLimpioDialogPorcentaje)
                    etPorcentaje.setText(tPorcentajeDialogPorcentaje.getText().toString().replace("-", ""));
                else
                    etPorcentaje.setText("");
                dialog.dismiss();

                break;
            case R.id.b_limpiar_dialog_porcentaje:
                etPrecioDialogPorcentaje.getText().clear();
                etLargaDialogPorcentaje.getText().clear();
                etCortaDialogPorcentaje.getText().clear();
                tPorcentajeDialogPorcentaje.setText("Porcentaje");
                etLargaDialogPorcentaje.setVisibility(View.VISIBLE);
                etCortaDialogPorcentaje.setVisibility(View.VISIBLE);
                tTituloLargaDialogPorcentaje.setVisibility(View.VISIBLE);
                tTituloCortaDialogPorcentaje.setVisibility(View.VISIBLE);
                seLimpioDialogPorcentaje = true;
                etPrecioDialogPorcentaje.requestFocus();
                break;
            case R.id.b_regresar_dialog_porcentaje:
                break;

        }


    };


    TextWatcher textWatcherDialogPorcentaje = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            if (!etPrecioDialogPorcentaje.getText().toString().isEmpty() &&
                    !etPrecioDialogPorcentaje.getText().toString().equals(".")) {

                if (etPrecisionDialogPorcentaje.getText().toString().isEmpty())
                    formatoDialogPorcentaje = "%,.5f";
                else {
                    formatoDialogPorcentaje = "%,." + etPrecisionDialogPorcentaje.getText().toString() + "f";
                }

                if (!etLargaDialogPorcentaje.getText().toString().isEmpty() &&
                        !etLargaDialogPorcentaje.getText().toString().equals(".")) {

                    if (seLimpioDialogPorcentaje)
                        seLimpioDialogPorcentaje = false;

                    precioDialogPorcentaje = Double.parseDouble(etPrecioDialogPorcentaje.getText().toString());
                    largaDialogPorcentaje = Double.parseDouble(etLargaDialogPorcentaje.getText().toString());


                    porcentajeDialogPorcentaje = largaDialogPorcentaje / precioDialogPorcentaje;
                    porcentajeDialogPorcentaje -= 1;
                    porcentajeDialogPorcentaje *= 100;
                    tPorcentajeDialogPorcentaje.setText(String.format(formatoDialogPorcentaje, porcentajeDialogPorcentaje));
                    cortaDialogPorcentaje = 0;
                } else if (!etCortaDialogPorcentaje.getText().toString().isEmpty() &&
                        !etCortaDialogPorcentaje.getText().toString().equals(".")

                ) {

                    if (seLimpioDialogPorcentaje)
                        seLimpioDialogPorcentaje = false;


                    precioDialogPorcentaje = Double.parseDouble(etPrecioDialogPorcentaje.getText().toString());
                    cortaDialogPorcentaje = Double.parseDouble(etCortaDialogPorcentaje.getText().toString());

                    porcentajeDialogPorcentaje = cortaDialogPorcentaje / precioDialogPorcentaje;
                    porcentajeDialogPorcentaje -= 1;
                    porcentajeDialogPorcentaje *= -1;
                    porcentajeDialogPorcentaje *= 100;
                    tPorcentajeDialogPorcentaje.setText(String.format(formatoDialogPorcentaje, porcentajeDialogPorcentaje));
                    largaDialogPorcentaje = 0;

                }
            }


        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    //TODO AJUSTANDO LA FUNCION


    View.OnLongClickListener onLongClickListener = view -> {
        switch (view.getId()) {
            case R.id.b_redondeo_ascendente:
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                crearDialogRedondeo(true);
                break;
            case R.id.b_redondeo_descendente:
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                crearDialogRedondeo(false);
                break;
            case R.id.b_apalancamiento:
                crearDialogApalancamiento();
                break;
            case R.id.t_titulo_cant:
                crearDialogPrecioPorcentaje(esDialogCantidad);
                break;

            case R.id.t_titulo_porcen:
                crearDialogPorcentaje();
                break;
            case R.id.t_titulo_referencia:
                crearDialogPrecioPorcentaje(esDialogReferencia);
                break;

            case R.id.t_titulo_tamano:

                hayDecimales = !hayDecimales;

                hayDecimalesDoble = hayDecimales;

                if (hayDecimales)
                    etPrecision.setVisibility(View.VISIBLE);
                else {
                    etPrecision.setVisibility(View.GONE);
                }


                if (!tTamanoC.getText().toString().equals("TPC")) {
                    if (!hayDecimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else {

                        if (precision.isEmpty())
                            tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                        else {

                            tTamanoC.setText(String.format("%,." +
                                    precision +
                                    "f", tamanoPosicionC));
                        }

                    }
                }

                if (!tTamano.getText().toString().equals("TP")) {
                    if (!hayDecimales) {
                        tTamano.setText(String.format("%,.0f", tamanoPosicion));
                    } else {
                        tTamano.setText(String.format("%,.2f", tamanoPosicion));
                    }
                }

                break;

        }
        return true;
    };

    View.OnClickListener clickListenerPosicion = view -> {


        switch (view.getId()) {

            case R.id.t_posicion_tamano:
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                crearDialogTamano();
                break;

            case R.id.t_posicion_tamano_conversion:

                if (!etReferencia.getText().toString().isEmpty() &&
                        etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");

                if (!tTamanoC.getText().toString().equals("TPC"))
                    crearDialogTamanoConversion();

                break;

            case R.id.t_posicion_lote:
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                crearDialogLotes();
                break;

            case R.id.t_posicion_lote_conversion:
                if (!etReferencia.getText().toString().isEmpty() &&
                        etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");

                if (!tTamanoC.getText().toString().equals("TPC"))
                    crearDialogLotesConversion();
                break;

            case R.id.t_margen:
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                crearDialogPrecioPorcentaje(esDialogMargen);
                break;

            case R.id.t_margenC:
                if (!etReferencia.getText().toString().isEmpty() &&
                        etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");

                if (!tTamanoC.getText().toString().equals("TPC"))
                    crearDialogPrecioPorcentaje(esDialogMargenConversion);
                break;
        }

    };

    View.OnClickListener onClickListener = view -> {

        switch (view.getId()) {
            case R.id.b_regresar_claro:

                if (seAplanoLimpiar) {

                    if (resCantidad == null)
                        break;

                    if (!resCantidad.equals(""))
                        etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(resCantidad)));
                    etCantidad.setText(resCantidad);
                    etPorcentaje.setText(resPorcentaje);
                    etReferencia.setText(resReferencia);
                    redondeoRef = ajusteRefRespaldo;
                    yaRedondeo = resYaRedondeo;
                    precioDialogPos = resPrecioXDialogPos;
                    porcentajeDialogPos = resPorcentajeXDialogPos;
                    seLimpioDialogPos = resSeLimpiDialogoPos;
                    hayDatosDialogReferencia = resHayDatosDialogReferencia;
                    hayDatosDialogCantidad = resHayDatosDialogCantidad;
                    cantidadDialogReferencia = resCantidadDialogReferencia;
                    precioDialogReferencia = resPrecioDialogReferencia;
                    precision = resPrecision;
                    etPrecision.setText(precision);
                    seAplanoLimpiar = false;
                    checarTituloReferencia();
                }
                break;

            case R.id.b_limpiar_claro:
                respaldoDeET();
                etCantidadMostrador.getText().clear();
                etPorcentaje.getText().clear();
                etReferencia.getText().clear();
                etPrecision.getText().clear();
                precision = "";
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
                resSeLimpiDialogoPos = seLimpioDialogPos;
                cantidadDialogReferencia = "";
                precioDialogReferencia = "";
                hayDatosDialogReferencia = false;
                hayDatosDialogCantidad = false;
                seLimpioDialogPos = true;
                seLimpioDialogPorcentaje = true;
                checarTituloReferencia();
                tMantener.setText("M");
                seAplanoMantener = false;
                etCantidadMostrador.setVisibility(View.VISIBLE);
                tCantidadMantener.setVisibility(View.GONE);
                break;


            case R.id.t_titulo_cant:
                respaldoDeET();
                etCantidadMostrador.getText().clear();
                yaRedondeo = false;
                hayDatosDialogCantidad = false;
                break;

            case R.id.t_titulo_porcen:
                respaldoDeET();
                etPorcentaje.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_referencia:
                respaldoDeET();
                etReferencia.getText().clear();
                yaRedondeo = false;
                hayDatosDialogReferencia = false;
                checarTituloReferencia();
                break;

            case R.id.t_titulo_tamano:

                hayDecimales = !hayDecimales;
                hayDecimalesDoble = false;

                if (hayDecimales)
                    etPrecision.setVisibility(View.VISIBLE);
                else {
                    etPrecision.setVisibility(View.GONE);
                }

                if (!tTamanoC.getText().toString().equals("TPC")) {
                    if (!hayDecimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else {

                        if (precision.isEmpty())
                            tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                        else {

                            tTamanoC.setText(String.format("%,." +
                                    precision +
                                    "f", tamanoPosicionC));
                        }
                    }
                }

                if (!tTamano.getText().toString().equals("TP")) {
                    tTamano.setText(String.format("%,.0f", tamanoPosicion));
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
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                ajusteRedondeo(false);
                break;


            case R.id.b_redondeo_ascendente:
                if (etCantidadMostrador.getText().toString().isEmpty())
                    etCantidadMostrador.setText("0");
                ajusteRedondeo(true);
                break;


            case R.id.b_id_mas:
                guardarDB();
                idOperacion += 1;
                bId.setText(String.valueOf(idOperacion));
                bLimpiarClaro.performClick();
                accederDB();
                cambioApalancamiento();
                break;

            case R.id.b_id_menos:
                guardarDB();
                idOperacion -= 1;
                if (idOperacion < 1)
                    idOperacion = 1;
                bId.setText(String.valueOf(idOperacion));
                bLimpiarClaro.performClick();
                accederDB();
                cambioApalancamiento();
                break;

            case R.id.b_borrarTodo:
                borrarTodoDb();
                break;

            case R.id.t_mantener:

                if (tTamano.getText().toString().equals("TP"))
                    break;
                seAplanoMantener = !seAplanoMantener;

                if (seAplanoMantener) {
                    tMantener.setText("A");
                    etCantidadMostrador.setVisibility(View.GONE);
                    tCantidadMantener.setVisibility(View.VISIBLE);

                    if (!etPorcentaje.getText().toString().isEmpty() &&
                            !etPorcentaje.getText().toString().equals(".")) {

                        porcentajeMantenerGuardado = Double.parseDouble(etPorcentaje.getText().toString());
                        double calculo = tamanoPosicion * porcentajeMantenerGuardado;
                        tCantidadMantener.setText(String.format("%,.2f", calculo / 100));
                    }
                    etPorcentaje.requestFocus();


                } else {
                    tMantener.setText("M");
                    etCantidadMostrador.setVisibility(View.VISIBLE);
                    tCantidadMantener.setVisibility(View.GONE);
                    etPorcentaje.setText(String.valueOf(porcentajeMantenerGuardado));


                    etPorcentaje.requestFocus();
                }
                break;

        }

    };

    private void borrarTodoDb() {
        Realm.init(getContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("xpos.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm -> {
            realm.deleteAll();
        });
        realm.close();
        sharedPreferences = getActivity().getSharedPreferences("xPos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idOperacion", 1);
        editor.apply();

        Intent i = getActivity().getIntent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void checarTituloReferencia() {
        if (hayDatosDialogReferencia)
            tTituloReferencia.setText("Referencia de conversion ^");
        else
            tTituloReferencia.setText("Referencia de conversion");
    }

    private void respaldoDeET() {
        seAplanoLimpiar = true;
        resCantidad = etCantidad.getText().toString();
        resCantidadMostrador = etCantidadMostrador.getText().toString();
        resPorcentaje = etPorcentaje.getText().toString();
        resReferencia = etReferencia.getText().toString();
        resYaRedondeo = yaRedondeo;
        resHayDatosDialogReferencia = hayDatosDialogReferencia;
        resHayDatosDialogCantidad = hayDatosDialogCantidad;
        ajusteRefRespaldo = redondeoRef;
        resCantidadDialogReferencia = cantidadDialogReferencia;
        resPrecioDialogReferencia = precioDialogReferencia;
        resPrecision = precision;
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

                if (hayDecimalesDoble)
                    tamanoPosicionAjustada = tamanoPosicion;
                else
                    tamanoPosicionAjustada = Math.round(tamanoPosicion);


                restante = tamanoPosicionAjustada % redondeoRef;
                restanteFinal = redondeoRef - restante;

                if (esAscendente)
                    num = tamanoPosicionAjustada + restanteFinal;
                else
                    num = tamanoPosicionAjustada - restante;

            } else {

                tamanoPosicionAjustada = tamanoPosicion;


                if (esAscendente)
                    num = tamanoPosicionAjustada + redondeoRef;

                else {
                    num = tamanoPosicionAjustada - redondeoRef;
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

            if (seAplanoMantener) {

                if (!etPorcentaje.getText().toString().isEmpty() &&
                        !etPorcentaje.getText().toString().equals(".")) {

                    double porcentaje = Double.parseDouble(etPorcentaje.getText().toString());
                    double calculo = tamanoPosicion * porcentaje;

                    tCantidadMantener.setText(String.format("%,.2f", calculo / 100));
                    return;
                }
                else {
                    tCantidadMantener.setText("0.00");
                    return;
                }

            }


            if (!etCantidad.getText().toString().isEmpty() &&
                    !etCantidad.getText().toString().equals(".") &&
                    !etPorcentaje.getText().toString().equals(".")

            ) {

                cantidad = Double.parseDouble(etCantidad.getText().toString());
                if (etPorcentaje.getText().toString().isEmpty()) {
                    porcentajeEntero = 1;
                }
                else
                    porcentajeEntero = Double.parseDouble(etPorcentaje.getText().toString());
                tamanoPosicion = cantidad / (porcentajeEntero / 100);


                if (!etReferencia.getText().toString().isEmpty() &&
                        !etReferencia.getText().toString().equals(".")) {

                    if (!hayDatosDialogReferencia)
                        referencia = Double.parseDouble(etReferencia.getText().toString());
                    else {
                        referencia = Double.valueOf(cantidadDialogReferencia) *
                                Double.valueOf(etReferencia.getText().toString());
                    }
                    tamanoPosicionC = tamanoPosicion / referencia;
                    loteC = tamanoPosicionC / 100000;
                    margenC = tamanoPosicionC / apalancamiento;
                    necesarioC = (cantidad / referencia) + margenC;
                    if (!hayDecimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else {

                        if (precision.isEmpty())
                            tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                        else {

                            tTamanoC.setText(String.format("%,." +
                                    precision +
                                    "f", tamanoPosicionC));
                        }
                    }


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


                if (!hayDecimalesDoble)
                    tTamano.setText(String.format("%,.0f", tamanoPosicion));
                else
                    tTamano.setText(String.format("%,.2f", tamanoPosicion));


                tLote.setText(String.format("%.4f", lote));
                tMargen.setText(String.format("%,.2f", margen));
                tSeguro.setText(String.format("%,.2f", necesario));

                if (yaRedondeo)
                    yaRedondeo = false;


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
        guardadoIdShared();
        guardarDB();
        super.onDestroy();
    }


    private void accederDB() {

        Realm.init(getContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("xpos.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        db = realm.where(Db.class).equalTo("id", idOperacion).findFirst();

        if (db == null) {
            realm.close();
            bLimpiarClaro.performClick();
            return;
        }

        if (db != null) {
            realm.executeTransaction(realm -> {

                if (db.getHayDatosDialogReferencia() != null)
                    hayDatosDialogReferencia = db.getHayDatosDialogReferencia();

                if (hayDatosDialogReferencia) {
                    cantidadDialogReferencia = db.getCantidadDialogReferencia();
                    precioDialogReferencia = db.getPrecioDialogReferencia();
                    if (!db.getValorDialogReferencia().equals(""))
                        valorDialogReferencia = Double.parseDouble(db.getValorDialogReferencia());
                }

                tipoApalancamiento = db.getTipoApalancamiento();

                if (db.getHayDecimales() != null) {
                    hayDecimales = db.getHayDecimales();
                    hayDecimalesDoble = db.getHayDecimalesDoble();
                    if (hayDecimales)
                        etPrecision.setVisibility(View.VISIBLE);
                    else
                        etPrecision.setVisibility(View.GONE);
                }

                if (db.getCantidad() != null) {

                    if (db.getCantidad().isEmpty()) {
                        etCantidadMostrador.setText("");
                    } else {
                        double valor = Double.valueOf(db.getCantidad());
                        etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(valor)));
                        etCantidad.setText(String.format("%.4f", Double.valueOf(valor)));
                    }
                }
                etPorcentaje.setText(db.getPorcentajeEntero());
                etReferencia.setText(db.getReferencia());
                redondeoRef = Double.parseDouble(db.getRedondeoRef());
                apalancamiento = db.getApalancamiento();
                precioDialogPos = Double.parseDouble(db.getPrecioDialogPos());
                porcentajeDialogPos = Double.parseDouble(db.getPorcentajeDialogPos());
                seLimpioDialogPos = db.getSeLimpioDIalogPos();
                precision = db.getPrecision();
                etPrecision.setText(precision);
                precioDialogPorcentaje = Double.parseDouble(db.getPrecioDialogPorcentaje());
                cortaDialogPorcentaje = Double.parseDouble(db.getCortaDialogPorcentaje());
                largaDialogPorcentaje = Double.parseDouble(db.getLargaDialogPorcentaje());
                seLimpioDialogPorcentaje = db.getSeLimpioDialogPorcentaje();

                checarTituloReferencia();

                if (db.getHayDatosDialogCantidad() != null) {
                    hayDatosDialogCantidad = db.getHayDatosDialogCantidad();
                    cantidadDialogCantidad = db.getCantidadDialogCantidad();
                    porcentajeDialogCantidad = db.getPorcentajeDialogCantidad();
                }

            });
        }
        realm.close();

    }


    Realm realm;
    Db db;

    public void guardarDB() {
        Realm.init(getContext());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("xpos.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        db = realm.where(Db.class).equalTo("id", idOperacion).findFirst();
        if (db == null)
            realm.executeTransaction(realm -> db = realm.createObject(Db.class, idOperacion));
        realm.executeTransaction(realm -> {

            db.setTipoApalancamiento(tipoApalancamiento);
            db.setHayDecimales(hayDecimales);
            db.setCantidad(etCantidad.getText().toString());
            db.setPorcentajeEntero(etPorcentaje.getText().toString());
            db.setReferencia(etReferencia.getText().toString());
            db.setRedondeoRef(String.valueOf(redondeoRef));
            db.setApalancamiento(apalancamiento);
            db.setPrecioDialogPos(String.valueOf(precioDialogPos));
            db.setPorcentajeDialogPos(String.valueOf(porcentajeDialogPos));
            db.setSeLimpioDIalogPos(seLimpioDialogPos);
            db.setPrecioDialogReferencia(precioDialogReferencia);
            db.setCantidadDialogReferencia(cantidadDialogReferencia);
            db.setHayDatosDialogReferencia(hayDatosDialogReferencia);
            db.setValorDialogReferencia(String.valueOf(valorDialogReferencia));
            db.setCantidadDialogCantidad(cantidadDialogCantidad);
            db.setPorcentajeDialogCantidad(porcentajeDialogCantidad);
            db.setHayDatosDialogCantidad(hayDatosDialogCantidad);
            db.setHayDecimalesDoble(hayDecimalesDoble);
            db.setPrecision(precision);
            db.setPrecioDialogPorcentaje(String.valueOf(precioDialogPorcentaje));
            db.setLargaDialogPorcentaje(String.valueOf(largaDialogPorcentaje));
            db.setCortaDialogPorcentaje(String.valueOf(cortaDialogPorcentaje));
            db.setSeLimpioDialogPorcentaje(seLimpioDialogPorcentaje);
        });
        realm.close();
    }

    private void guardadoIdShared() {
        sharedPreferences = getActivity().getSharedPreferences("xPos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idOperacion", idOperacion);
        editor.apply();
    }

    private void recuperarIdShared() {
        sharedPreferences = getActivity().getSharedPreferences("xPos", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("idOperacion"))
            idOperacion = sharedPreferences.getInt("idOperacion", 1);
        else
            idOperacion = 1;
    }


    private void copyToast(CharSequence text) {

        ClipData clip = ClipData.newPlainText("Precio", text.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Precio grabada: " + text.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        super.onPause();
    }
}
