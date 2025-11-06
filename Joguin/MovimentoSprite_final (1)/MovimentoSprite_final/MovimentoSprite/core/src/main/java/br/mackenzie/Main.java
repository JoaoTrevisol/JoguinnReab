package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;

    Fundo fundo;
    Personagem personagem;

    Array<Fantasma> fantasmas;
    float tempoGerarFantasma = 0f;
    float intervaloGeracao = 3f;
    float velocidadeFantasma = 2.5f;

    boolean personagemAtivo = true;

    // Controle de brilho
    private Texture fundoBrilho;
    private float brilho = 1f; // 1 = tela clara, 0 = escura
    private final float velocidadeEscurecer = 0.3f;
    private final float velocidadeClarear = 0.5f;


    @Override
    public void create() {
        if (spriteBatch == null) spriteBatch = new SpriteBatch();
        if (viewport == null) viewport = new FitViewport(8, 5);

        fundo = new Fundo();
        personagem = new Personagem();
        personagem.centralizar(viewport);

        fantasmas = new Array<>();
        tempoGerarFantasma = 0f;
        personagemAtivo = true;

        // Cria textura preta para o controle de brilho
        if (fundoBrilho == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            fundoBrilho = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Reinicia o jogo ao apertar R
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            reiniciarJogo();
            return;
        }

        if (personagemAtivo) {
            input(delta);
            personagem.update(delta, viewport);
        }

        fundo.atualizar(delta);

        // Atualiza o brilho (S = clareia)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            brilho += velocidadeClarear * delta;
        } else {
            brilho -= velocidadeEscurecer * delta;
        }
        brilho = MathUtils.clamp(brilho, 0f, 1f);

        // Aumenta velocidade conforme troca o fundo
        switch (fundo.getIndiceAtual()) {
            case 0: velocidadeFantasma = 2.5f; break;
            case 1: velocidadeFantasma = 3.5f; break;
            case 2: velocidadeFantasma = 5f; break;
        }

        // Geração de fantasmas
        tempoGerarFantasma += delta;
        if (tempoGerarFantasma >= intervaloGeracao) {
            tempoGerarFantasma = 0f;
            float alturaChao = 0f;
            fantasmas.add(new Fantasma(viewport, alturaChao, velocidadeFantasma));
        }

        // Atualiza fantasmas e verifica colisões
        for (int i = fantasmas.size - 1; i >= 0; i--) {
            Fantasma f = fantasmas.get(i);
            f.update(delta);

            // Verifica colisão
            if (personagemAtivo && f.getHitbox().overlaps(personagem.getHitbox())) {
                personagemAtivo = false; // personagem desaparece
            }

            if (f.saiuDaTela()) {
                f.dispose();
                fantasmas.removeIndex(i);
            }
        }

        // Renderização
        ScreenUtils.clear(Color.WHITE);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        fundo.render(spriteBatch, viewport);
        if (personagemAtivo) personagem.render(spriteBatch);
        for (Fantasma f : fantasmas) f.render(spriteBatch);

        // Camada escura (controle de brilho)
        spriteBatch.setColor(0, 0, 0, 1f - brilho);
        spriteBatch.draw(fundoBrilho, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.setColor(Color.WHITE);

        spriteBatch.end();
    }

    private void reiniciarJogo() {
        // Libera recursos
        dispose();

        // Recria tudo
        create();
    }

    private void input(float delta) {
        float velocidade = 1f;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            personagem.moverDireita(delta, velocidade);
            fundo.moverDireita(delta, velocidade);
        } else {
            personagem.idle();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            personagem.pular();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        if (fundo != null) fundo.dispose();
        if (personagem != null) personagem.dispose();
        if (fantasmas != null) {
            for (Fantasma f : fantasmas) f.dispose();
            fantasmas.clear();
        if (fundoBrilho != null) fundoBrilho.dispose();
        }
    }
}
