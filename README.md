# 🐾 Pokedex Kotlin

![Pokedex Banner](https://raw.githubusercontent.com/rafaelfemina/pokedexkotlin/main/banner.png)  

Um aplicativo **Pokedex** desenvolvido em **Kotlin** para Android, inspirado na franquia Pokémon. O projeto demonstra boas práticas de desenvolvimento Android, utilizando **MVVM**, consumo de APIs, RecyclerView, Adapters e Kotlin Coroutines.

---

## 🎯 Objetivo do Projeto

O objetivo deste projeto é criar um aplicativo simples, porém funcional, que permita aos usuários visualizar informações sobre diversos Pokémon, incluindo:

- Nome
- Imagem
- Tipo
- Detalhes básicos

Além disso, serve como estudo de arquitetura **MVVM** e integração com APIs em Kotlin.

---

## 🏗 Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)**, organizado da seguinte forma:

br.com.rafaelfemina.android.pokedex_kotlin
│
├─ model/ # Classes de dados (Pokemon, API Response, etc.)
├─ repository/ # Camada de acesso a dados (API ou local)
├─ view/ # Activities, Fragments e Adapters
├─ viewmodel/ # Lógica de UI e LiveData
└─ utils/ # Classes utilitárias (ex: cores, imagens, funções auxiliares)

markdown
Copiar código

**Fluxo resumido:**

1. `View` observa o `ViewModel` via LiveData.
2. `ViewModel` solicita dados do `Repository`.
3. `Repository` consome a API ou recursos locais.
4. Dados retornam e a `View` atualiza automaticamente.

---

## ⚙ Tecnologias e Ferramentas

- **Kotlin**
- **Android Studio**
- **MVVM Architecture**
- **RecyclerView / Adapter**
- **Kotlin Coroutines**
- **LiveData**
- **Retrofit2**
- **Glide** (para carregamento de imagens)
- **Material Design Components**

---

## 📦 Funcionalidades

- 📌 Listagem de Pokémon com imagens e nomes.
- 🔍 Busca por Pokémon.
- 🌈 Diferenciação por tipos com cores.
- 💡 Layout simples e responsivo para Android.

---

## 🚀 Como Rodar o Projeto

1. Clone o repositório:

```bash
git clone https://github.com/rafaelfemina/pokedexkotlin.git
Abra o projeto no Android Studio.
```
Sincronize as dependências do Gradle.

Execute em um emulador ou dispositivo físico.

🧩 Observações
Todos os recursos foram implementados em Kotlin.

O projeto foca em simplicidade e organização.

Arquitetura MVVM garante fácil manutenção e escalabilidade.

Funciona em dispositivos com Android 7.0+ (API 24).

📷 Preview


🔗 Links
GitHub: rafaelfemina/pokedexkotlin

Feito com ❤️ por Rafael Femina
