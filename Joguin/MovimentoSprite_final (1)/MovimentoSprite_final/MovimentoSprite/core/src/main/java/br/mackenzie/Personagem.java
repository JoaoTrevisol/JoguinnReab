package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Personagem {
    // VARIÁVEIS DE ANIMAÇÃO
    private Animation<TextureRegion> animacaoCorridaIntro; // Nova: Run1, Run2, Run3 (NORMAL)
    private Animation<TextureRegion> animacaoCorridaLoop;  // Nova: Run4 a Run8 (LOOP)
    private Animation<TextureRegion> animacaoPulo;

    private float stateTimeCorrida; // Usado para ambas as animações de corrida
    private float stateTimePulo;
    private TextureRegion frameAtual;

    // NOVO: Flag para saber se a introdução da corrida já terminou
    private boolean introCorridaTerminou = false;

    // VARIÁVEIS DO PERSONAGEM
    private Rectangle hitbox;
    private float posX, posY;
    private boolean olhandoDireita = true;

    private boolean noChao = true;
    private float velY = 0f;
    private final float gravidade = -15f;
    private final float forcaPulo = 9f;

    private final float largura = 2f;
    private final float altura = 2f;

    public Personagem() {
        // --- PREPARAÇÃO DOS FRAMES ---
        TextureRegion[] framesCompletos = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            Texture t = new Texture("Run" + (i + 1) + ".png");
            framesCompletos[i] = new TextureRegion(t);
        }

        // --- 1. CRIAÇÃO DA ANIMAÇÃO INTRO (Run1, Run2, Run3) ---
        TextureRegion[] framesIntro = new TextureRegion[3];
        // Copia os índices 0, 1, 2
        System.arraycopy(framesCompletos, 0, framesIntro, 0, 3);

        animacaoCorridaIntro = new Animation<>(0.1f, framesIntro);
        // Garante que a intro rode APENAS UMA VEZ
        animacaoCorridaIntro.setPlayMode(Animation.PlayMode.NORMAL);

        // --- 2. CRIAÇÃO DA ANIMAÇÃO LOOP (Run4 a Run8) ---
        TextureRegion[] framesLoop = new TextureRegion[4];
        System.arraycopy(framesCompletos, 3, framesLoop, 0, 4);

        animacaoCorridaLoop = new Animation<>(0.1f, framesLoop);
        animacaoCorridaLoop.setPlayMode(Animation.PlayMode.LOOP);

        // --- ANIMAÇÃO DE PULO (inalterada) ---
        TextureRegion[] framesPulo = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            Texture t = new Texture("Jump" + (i + 1) + ".png");
            framesPulo[i] = new TextureRegion(t);
        }
        animacaoPulo = new Animation<>(0.1f, framesPulo);

        // Frame inicial é o primeiro da INTRO (Run1.png)
        frameAtual = framesIntro[0];
        hitbox = new Rectangle(posX, posY, largura, altura);
    }

    // ... (centralizar, update, pular, render, getHitbox inalterados) ...

    public void centralizar(FitViewport viewport) {
        posX = (viewport.getWorldWidth() - largura) / 2f;
        posY = 0;
        hitbox.set(posX, posY, largura, altura);
    }

    public void update(float delta, FitViewport viewport) {
        if (!noChao) {
            velY += gravidade * delta;
            posY += velY * delta;

            stateTimePulo += delta;
            frameAtual = animacaoPulo.getKeyFrame(stateTimePulo, false);

            if (posY <= 0) {
                posY = 0;
                velY = 0;
                noChao = true;
                // Reseta a flag e volta para o frame Idle (Run1.png)
                introCorridaTerminou = false;
                frameAtual = animacaoCorridaIntro.getKeyFrames()[0];
            }
        }

        hitbox.set(posX, posY, largura, altura);
    }

    public void moverDireita(float delta, float velocidade) {
        olhandoDireita = true;
        if (noChao) {
            stateTimeCorrida += delta;

            // Lógica de Transição da Animação
            if (!introCorridaTerminou) {
                // Roda a INTRO (Run1 a Run3)
                frameAtual = animacaoCorridaIntro.getKeyFrame(stateTimeCorrida);

                // Verifica se a intro acabou
                if (animacaoCorridaIntro.isAnimationFinished(stateTimeCorrida)) {
                    introCorridaTerminou = true; // Seta a flag para true
                    // Não é necessário resetar stateTimeCorrida, pois o loop começa
                    // a contar de onde a intro parou.
                }
            }

            if (introCorridaTerminou) {
                // Roda o LOOP (Run4 a Run8)
                frameAtual = animacaoCorridaLoop.getKeyFrame(stateTimeCorrida);
            }
        }
    }

    public void moverEsquerda(float delta, float velocidade) {
        olhandoDireita = false;
        if (noChao) {
            stateTimeCorrida += delta;

            // Lógica de Transição da Animação
            if (!introCorridaTerminou) {
                frameAtual = animacaoCorridaIntro.getKeyFrame(stateTimeCorrida);

                if (animacaoCorridaIntro.isAnimationFinished(stateTimeCorrida)) {
                    introCorridaTerminou = true;
                }
            }

            if (introCorridaTerminou) {
                frameAtual = animacaoCorridaLoop.getKeyFrame(stateTimeCorrida);
            }
        }
    }

    public void idle() {
        if (noChao) {
            // No estado Idle, resetamos o tempo de estado e a flag, e mostramos o primeiro frame.
            stateTimeCorrida = 0f;
            introCorridaTerminou = false;
            frameAtual = animacaoCorridaIntro.getKeyFrames()[0];
        }
    }

    public void pular() {
        if (noChao) {
            velY = forcaPulo;
            noChao = false;
            stateTimePulo = 0f;
            // Ao pular, resetamos a animação de corrida para recomeçar a intro ao pousar.
            stateTimeCorrida = 0f;
            introCorridaTerminou = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (olhandoDireita) {
            batch.draw(frameAtual, posX, posY, largura, altura);
        } else {
            batch.draw(frameAtual, posX + largura, posY, -largura, altura);
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void dispose() {
        // Você precisa fazer dispose de todos os frames completos.
        // Como 'framesCompletos' não é uma variável de instância,
        // precisamos garantir que todos os TextureRegions criados sejam descartados.
        // A maneira mais segura aqui é iterar por todos os frames usados nas ANIMAÇÕES.
        // O LibGDX garante que TextureRegions referenciando a mesma Texture
        // apenas farão dispose uma vez.

        // Itera sobre frames da Intro e Pulo (cuidado para não dar dispose na mesma Texture duas vezes)
        for (TextureRegion t : animacaoCorridaIntro.getKeyFrames()) t.getTexture().dispose();
        // A animação Loop usa as Textures Run4-Run8 que já estão nos framesCompletos
        // Se você não quiser ter problemas de duplo dispose, a maneira mais robusta
        // seria guardar todos os objetos Texture que foram criados e dar dispose neles.

        // Vamos manter o seu dispose original e adicionar o novo loop:
        for (TextureRegion t : animacaoCorridaLoop.getKeyFrames()) t.getTexture().dispose();
        for (TextureRegion t : animacaoPulo.getKeyFrames()) t.getTexture().dispose();
    }
}
