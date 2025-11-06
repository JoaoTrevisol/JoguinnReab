package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Fundo {
    private Texture[] fundos;
    private int indiceAtual = 0;
    private float tempoTroca = 60f; // troca a cada 60 segundos
    private float tempoAtual = 0f;

    private float offsetX = 0f;
    private float velocidadeParalaxe = 1f;

    public Fundo() {
        fundos = new Texture[]{
            new Texture("tela_fundo1.png"),
            new Texture("tela_fundo2.png"),
            new Texture("tela_fundo3.png")
        };
    }

    public void atualizar(float delta) {
        tempoAtual += delta;
        if (indiceAtual < fundos.length - 1 && tempoAtual >= tempoTroca) {
            tempoAtual = 0;
            indiceAtual++;
        }
    }

    public void moverDireita(float delta, float velocidade) {
        offsetX -= velocidade * delta * velocidadeParalaxe;
    }

    public void moverEsquerda(float delta, float velocidade) {
        offsetX += velocidade * delta * velocidadeParalaxe;
    }

    public void render(SpriteBatch batch, FitViewport viewport) {
        float largura = viewport.getWorldWidth();
        float altura = viewport.getWorldHeight();
        float x1 = offsetX % largura;
        if (x1 > 0) x1 -= largura;

        Texture fundo = fundos[indiceAtual];
        batch.draw(fundo, x1, 0, largura, altura);
        batch.draw(fundo, x1 + largura, 0, largura, altura);
    }

    public int getIndiceAtual() {
        return indiceAtual;
    }

    public void dispose() {
        for (Texture f : fundos) f.dispose(); }
    }