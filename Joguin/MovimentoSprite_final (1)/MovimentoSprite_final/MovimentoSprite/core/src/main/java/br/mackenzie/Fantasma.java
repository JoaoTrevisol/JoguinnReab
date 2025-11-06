package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation; // Importa Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Importa TextureRegion
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Fantasma {
    // Substituímos 'textura' por 'animacao' e 'frameAtual'
    private Animation<TextureRegion> animacao;
    private float stateTime; // Para controlar o tempo da animação
    private TextureRegion frameAtual; // O frame a ser desenhado no momento

    private float x, y;
    private float largura = 0.6f; // Mantenha a largura e altura consistentes com seus sprites
    private float altura = 1f;
    private float velocidade;
    private Rectangle hitbox;

    public Fantasma(FitViewport viewport, float y, float velocidade) {
        this.y = y;
        this.velocidade = velocidade;

        // --- Configuração da Animação da Caveira ---
        // Array para armazenar os 4 frames
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            // Supondo que seus arquivos se chamem "fantasma1.png", "fantasma2.png", etc.
            // Ajuste o nome base "Ghost" conforme o nome real dos seus arquivos.
            Texture t = new Texture("Caveira" + (i + 1) + ".png");
            frames[i] = new TextureRegion(t);
        }
        // Cria a animação: 0.1s de duração por frame, e modo de repetição LOOP
        animacao = new Animation<>(0.1f, frames);
        animacao.setPlayMode(Animation.PlayMode.LOOP); // Faz a animação se repetir

        // Inicializa o tempo de estado e o frame atual
        stateTime = 0f;
        frameAtual = animacao.getKeyFrame(stateTime);

        // A posição X inicial deve ser fora da tela, à direita
        this.x = viewport.getWorldWidth() + largura;
        this.hitbox = new Rectangle(x, y, largura, altura);
    }

    public void update(float delta) {
        stateTime += delta; // Atualiza o tempo da animação
        frameAtual = animacao.getKeyFrame(stateTime); // Pega o frame atual da animação

        x -= velocidade * delta; // Move a caveira para a esquerda
        hitbox.setPosition(x, y); // Atualiza a posição da hitbox
    }

    public void render(SpriteBatch batch) {
        // Desenha o frame atual da animação
        batch.draw(frameAtual, x, y, largura, altura);
    }

    public boolean saiuDaTela() {
        return x + largura < 0;
    }
    public Rectangle getHitbox() {
        return hitbox;
    }

    public void dispose() {
        // Dispor de todas as Texturas usadas nos frames da animação
        for (TextureRegion t : animacao.getKeyFrames()) {
            t.getTexture().dispose();
        }
    }
}
