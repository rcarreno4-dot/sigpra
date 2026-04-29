# Publicar En GitHub (Repositorio Publico)

Usuario: `Fabianbarrera13`

Repositorio sugerido:
`https://github.com/Fabianbarrera13/PROYECTO_VISTAS_NETBEANS`

## Opcion A: con Git (recomendado)
Ejecuta en tu terminal local donde SI tengas `git` instalado:

```bash
cd "c:\Users\Fabian\OneDrive\Documentos\app horas\PROYECTO_VISTAS_NETBEANS"
git init
git add .
git commit -m "Codigo documentado y organizacion de entrega"
git branch -M main
git remote add origin https://github.com/Fabianbarrera13/PROYECTO_VISTAS_NETBEANS.git
git push -u origin main
```

## Opcion B: desde web (sin git)
1. Crear repo publico `PROYECTO_VISTAS_NETBEANS` en GitHub.
2. Click en `Add file` > `Upload files`.
3. Arrastrar el contenido de esta carpeta.
4. Commit directo en `main`.

## Verificacion minima
Deben verse en el repo:
- `README.md`
- `docs/README.md`
- `docs/01_codigo_documentado/CODIGO_DOCUMENTADO.md`
- `docs/02_base_datos/bd/BD_SOLO_TABLAS_LIMPIO.txt`
- `docs/02_base_datos/bd/BD_SOLO_USUARIOS_LIMPIO.txt`
- `docs/03_ui/WIREFRAME_VISTAS.md`
