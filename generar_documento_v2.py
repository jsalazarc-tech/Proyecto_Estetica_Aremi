# -*- coding: utf-8 -*-
"""
Generador del 2° Avance PI - Salón de Belleza AREMI
Versión 2 COMPLETA: incluye diagrama de casos de uso (ASCII), diagramas de
actividades, MER como tabla, y mockups descriptivos.
"""

import os
from docx import Document
from docx.shared import Pt, Cm, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
import copy

# ─────────────────────────────────────────────
# HELPERS
# ─────────────────────────────────────────────

def set_cell_bg(cell, hex_color):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    shd = OxmlElement('w:shd')
    shd.set(qn('w:val'), 'clear')
    shd.set(qn('w:color'), 'auto')
    shd.set(qn('w:fill'), hex_color)
    tcPr.append(shd)

def set_cell_borders(cell, top=True, bottom=True, left=True, right=True, color="999999"):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    tcBorders = OxmlElement('w:tcBorders')
    for side, do in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        if do:
            el = OxmlElement(f'w:{side}')
            el.set(qn('w:val'), 'single')
            el.set(qn('w:sz'), '4')
            el.set(qn('w:color'), color)
            tcBorders.append(el)
    tcPr.append(tcBorders)

def add_heading(doc, text, level=1, color="1a1a2e"):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    run = p.add_run(text)
    run.bold = True
    sizes = {1: 14, 2: 12, 3: 11}
    run.font.size = Pt(sizes.get(level, 11))
    run.font.color.rgb = RGBColor.from_string(color)
    p.paragraph_format.space_before = Pt(14 if level == 1 else 10)
    p.paragraph_format.space_after = Pt(6)
    return p

def add_body(doc, text, justify=True):
    p = doc.add_paragraph(text)
    p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY if justify else WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_after = Pt(6)
    for run in p.runs:
        run.font.size = Pt(12)
        run.font.name = 'Times New Roman'
    return p

def add_bullet(doc, text, level=1):
    p = doc.add_paragraph(style='List Bullet')
    run = p.add_run(text)
    run.font.size = Pt(12)
    run.font.name = 'Times New Roman'
    p.paragraph_format.left_indent = Cm(1.27 * level)
    return p

def add_monospace(doc, text):
    """Agrega bloque de código/ASCII con fuente monoespaciada."""
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    run = p.add_run(text)
    run.font.name = 'Courier New'
    run.font.size = Pt(9)
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(2)
    return p

def add_table_caption(doc, number, caption):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.LEFT
    r1 = p.add_run(f'Tabla {number}. ')
    r1.bold = True
    r1.font.size = Pt(11)
    r1.font.name = 'Times New Roman'
    r2 = p.add_run(caption)
    r2.italic = True
    r2.font.size = Pt(11)
    r2.font.name = 'Times New Roman'
    p.paragraph_format.space_before = Pt(10)
    p.paragraph_format.space_after = Pt(2)
    return p

def add_figure_caption(doc, number, caption):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r1 = p.add_run(f'Figura {number}. ')
    r1.bold = True
    r1.font.size = Pt(11)
    r1.font.name = 'Times New Roman'
    r2 = p.add_run(caption)
    r2.italic = True
    r2.font.size = Pt(11)
    r2.font.name = 'Times New Roman'
    p.paragraph_format.space_after = Pt(10)
    return p

def add_note(doc, text):
    p = doc.add_paragraph()
    r = p.add_run(f'Nota. {text}')
    r.italic = True
    r.font.size = Pt(10)
    r.font.name = 'Times New Roman'
    p.paragraph_format.space_after = Pt(10)
    return p

def header_row(table, texts, bg="16213E"):
    row = table.rows[0]
    for i, text in enumerate(texts):
        cell = row.cells[i]
        cell.text = ''
        run = cell.paragraphs[0].add_run(text)
        run.bold = True
        run.font.color.rgb = RGBColor(255, 255, 255)
        run.font.size = Pt(10)
        run.font.name = 'Times New Roman'
        cell.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER
        set_cell_bg(cell, bg)

def data_row(table, row_idx, texts, bg=None):
    row = table.rows[row_idx]
    for i, text in enumerate(texts):
        cell = row.cells[i]
        cell.text = ''
        run = cell.paragraphs[0].add_run(text)
        run.font.size = Pt(10)
        run.font.name = 'Times New Roman'
        cell.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.LEFT
        if bg:
            set_cell_bg(cell, bg)

# ─────────────────────────────────────────────
# DOCUMENTO PRINCIPAL
# ─────────────────────────────────────────────

doc = Document()

# Márgenes APA 7: 2.54 cm
for section in doc.sections:
    section.top_margin    = Cm(2.54)
    section.bottom_margin = Cm(2.54)
    section.left_margin   = Cm(2.54)
    section.right_margin  = Cm(2.54)

# Estilo base
style = doc.styles['Normal']
style.font.name = 'Times New Roman'
style.font.size = Pt(12)
style.paragraph_format.line_spacing = Pt(18)  # 1.5 interlineado

# ══════════════════════════════════════════════════════════════════
# PORTADA
# ══════════════════════════════════════════════════════════════════
doc.add_page_break()

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
p.add_run('\n\n')

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('INSTITUCIÓN UNIVERSITARIA ANTONIO JOSÉ CAMACHO')
run.bold = True; run.font.size = Pt(13); run.font.name = 'Times New Roman'

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('Facultad de Ingeniería')
run.font.size = Pt(12); run.font.name = 'Times New Roman'

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('Programa de Tecnología en Sistemas e Informática')
run.font.size = Pt(12); run.font.name = 'Times New Roman'

doc.add_paragraph()
doc.add_paragraph()

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('SOFTWARE DE GESTIÓN DE CITAS Y LIQUIDACIÓN DE COMISIONES\nPARA EL SALÓN DE BELLEZA AREMI')
run.bold = True; run.font.size = Pt(16); run.font.name = 'Times New Roman'
run.font.color.rgb = RGBColor(22, 33, 62)

doc.add_paragraph()
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('Segundo Avance del Proyecto Integrador — Ingeniería de Software I')
run.italic = True; run.font.size = Pt(12); run.font.name = 'Times New Roman'

doc.add_paragraph()
doc.add_paragraph()

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run(
    'Erick Johan Enríquez Vera\n'
    'Jeram Jeamdro Salazar Calderón\n'
    'Yeneicy Alexandra Michileno Salcedo'
)
run.font.size = Pt(12); run.font.name = 'Times New Roman'

doc.add_paragraph()
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('Grupo: 03')
run.font.size = Pt(12); run.font.name = 'Times New Roman'

doc.add_paragraph()
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run(
    'Docentes:\nIng. [Nombre docente Programación II]\nIng. [Nombre docente Ingeniería de Software I]'
)
run.font.size = Pt(12); run.font.name = 'Times New Roman'

doc.add_paragraph()
doc.add_paragraph()
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('Santiago de Cali, Colombia\nNoviembre de 2026')
run.font.size = Pt(12); run.font.name = 'Times New Roman'

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# RESUMEN
# ══════════════════════════════════════════════════════════════════
add_heading(doc, 'Resumen', 1)
add_body(doc,
    'El presente proyecto de ingeniería de software propone el diseño, desarrollo e '
    'implementación de un sistema de información de escritorio para el Salón de Belleza Aremi, '
    'ubicado en Santiago de Cali, Colombia. El problema identificado corresponde a la gestión '
    'manual de citas, servicios y liquidaciones de comisiones, proceso que genera conflictos de '
    'agenda, errores en los cálculos de pago al personal y ausencia de trazabilidad histórica. '
    'El objetivo principal consiste en automatizar estos procesos mediante un software desarrollado '
    'en Java bajo la arquitectura Modelo-Vista-Controlador (MVC) con el patrón de acceso a datos '
    'DAO (Data Access Object) y base de datos relacional MySQL. La metodología adoptada es el '
    'desarrollo incremental, organizado en cinco incrementos funcionales progresivos. '
    'Como resultados alcanzados en este segundo avance, se documentan: la especificación completa '
    'de diez requerimientos funcionales con criterios de aceptación individuales en formato Gherkin, '
    'siete requerimientos no funcionales con métricas de verificación, el refinamiento de ocho casos '
    'de uso, el diagrama de clases del dominio real (incluyendo ServicioRealizado y '
    'LiquidacionComision), el modelo entidad-relación de la base de datos, el cronograma incremental '
    'con responsables y entregables, y la arquitectura MVC+DAO refactorizada y documentada en '
    'código fuente limpio. Como resultados esperados para los incrementos siguientes, se proyecta '
    'la validación funcional de los módulos de liquidación y emisión de PDF con usuarios reales del '
    'salón, así como las pruebas de integración del sistema completo.'
)
p = doc.add_paragraph()
run = p.add_run('Palabras clave: ')
run.bold = True; run.font.size = Pt(12); run.font.name = 'Times New Roman'
run2 = p.add_run(
    'gestión de citas, liquidación de comisiones, metodología incremental, '
    'Modelo-Vista-Controlador, patrón DAO, Java Swing, MySQL, ingeniería de software.'
)
run2.italic = True; run2.font.size = Pt(12); run2.font.name = 'Times New Roman'

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# TABLA DE CONTENIDO (manual, referencial)
# ══════════════════════════════════════════════════════════════════
add_heading(doc, 'Tabla de Contenido', 1)
toc_items = [
    ('1. Introducción', '4'),
    ('2. Marco del Problema', '4'),
    ('   2.1 Planteamiento del Problema', '4'),
    ('   2.2 Pregunta de Investigación', '5'),
    ('   2.3 Alcance del Proyecto y Delimitación', '5'),
    ('3. Justificación', '5'),
    ('4. Objetivos', '6'),
    ('5. Marco Referencial', '6'),
    ('6. Diseño Metodológico: Desarrollo Incremental', '7'),
    ('7. Requerimientos del Sistema', '9'),
    ('   7.1 Requerimientos Funcionales (RF-01 a RF-10)', '9'),
    ('   7.2 Requerimientos No Funcionales (RNF-01 a RNF-07)', '14'),
    ('8. Casos de Uso y Matriz de Roles', '15'),
    ('   8.1 Matriz de Roles y Permisos (RBAC)', '15'),
    ('   8.2 Especificación de Casos de Uso (CU-01 a CU-08)', '16'),
    ('   8.3 Diagrama General de Casos de Uso (UML)', '17'),
    ('9. Diagramas de Actividades', '18'),
    ('   9.1 Proceso de Agendamiento de Citas', '18'),
    ('   9.2 Proceso de Liquidación de Comisiones', '19'),
    ('10. Diseño Técnico y Arquitectónico', '19'),
    ('   10.1 Arquitectura MVC y Patrón DAO', '19'),
    ('   10.2 Diagrama de Clases UML del Dominio Real', '20'),
    ('   10.3 Modelo Entidad-Relación (MER)', '21'),
    ('   10.4 Reglas Matemáticas de Comisiones', '22'),
    ('11. Plan de Pruebas y Matriz de Verificación', '22'),
    ('12. Mockups de la Aplicación', '23'),
    ('13. Conclusiones', '24'),
    ('14. Referencias Bibliográficas (APA 7)', '25'),
]
for item, page in toc_items:
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(2)
    tab_stops = p.paragraph_format.tab_stops
    r = p.add_run(f'{item}')
    r.font.size = Pt(11); r.font.name = 'Times New Roman'

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 1. INTRODUCCIÓN
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '1. Introducción', 1)
add_body(doc,
    'En la dinámica económica contemporánea, el sector del cuidado personal y la estética integral '
    'en Colombia ha experimentado un crecimiento sostenido, particularmente en ciudades intermedias '
    'como Santiago de Cali. Según el Departamento Administrativo Nacional de Estadística (DANE, 2022), '
    'las microempresas del sector servicios personales representan uno de los segmentos con mayor '
    'generación de empleo informal. Sin embargo, la adopción tecnológica en estos establecimientos '
    'permanece significativamente rezagada respecto a otros sectores, lo cual se traduce en ineficiencias '
    'operativas que limitan su competitividad y crecimiento (Sommerville, 2019).'
)
add_body(doc,
    'El Salón de Belleza Aremi, ubicado en la ciudad de Santiago de Cali, enfrenta directamente '
    'esta problemática. La administración de citas se realiza en agendas físicas propensas a errores '
    'de duplicidad y cruce de horarios; el cálculo de comisiones del personal se efectúa manualmente '
    'con hojas de cálculo, generando inconsistencias y desconfianza; y la ausencia de registros '
    'históricos impide analizar el desempeño del negocio o del personal. Esta situación compromete '
    'tanto la satisfacción de las clientas como la motivación de las trabajadoras, quienes dependen '
    'de que sus comisiones sean calculadas con exactitud y transparencia (Pressman y Maxim, 2020).'
)
add_body(doc,
    'Como respuesta de ingeniería de software a las necesidades puntuales identificadas en Aremi, '
    'el presente proyecto propone el desarrollo de un sistema de escritorio en Java que automatice '
    'integralmente los procesos de agendamiento de citas, registro de servicios realizados y '
    'liquidación periódica de comisiones, con emisión de comprobantes en formato PDF. '
    'El sistema implementa la arquitectura Modelo-Vista-Controlador (MVC) con el patrón de acceso '
    'a datos DAO, garantizando la separación de responsabilidades, la mantenibilidad del código '
    'fuente y la posibilidad de evolución tecnológica (Martin, 2017; Gamma et al., 1994).'
)
add_body(doc,
    'El presente informe documenta de manera detallada el Segundo Avance del Proyecto Integrador '
    'correspondiente a la asignatura Ingeniería de Software I. El documento se organiza de la '
    'siguiente manera: se presenta en primer lugar el marco del problema y la justificación, '
    'seguidos de los objetivos y el marco referencial; a continuación se desarrolla el diseño '
    'metodológico incremental con cronograma y responsables; posteriormente se especifican los '
    'diez requerimientos funcionales y siete no funcionales; se documentan los casos de uso, '
    'diagramas de actividades y el diseño técnico-arquitectónico; finalmente se incluye el plan '
    'de pruebas, los mockups y las conclusiones, respaldados por catorce referencias académicas '
    'en formato APA 7.'
)

# ══════════════════════════════════════════════════════════════════
# 2. MARCO DEL PROBLEMA
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '2. Marco del Problema', 1)
add_heading(doc, '2.1 Planteamiento del Problema', 2)
add_body(doc,
    'El Salón de Belleza Aremi experimenta una serie de fallas operativas derivadas de la '
    'dependencia de procesos manuales en cuatro áreas críticas: (a) la gestión de citas se '
    'realiza en cuadernos físicos sin mecanismo de validación de disponibilidad, generando '
    'conflictos de horario; (b) el seguimiento de servicios prestados no cuenta con un registro '
    'digital que preserve historial; (c) la liquidación de comisiones se calcula manualmente '
    'cada quincena, sin mecanismo que congele los valores históricos ni prevenga liquidaciones '
    'duplicadas; y (d) no existe un sistema de autenticación que diferencie los permisos de la '
    'administradora respecto a los de las empleadas.'
)

add_heading(doc, '2.2 Pregunta de Investigación', 2)
add_body(doc,
    '¿De qué manera un sistema de información desarrollado bajo la arquitectura Modelo-Vista-Controlador '
    'con el patrón DAO y base de datos relacional MySQL puede mejorar la eficiencia operativa del '
    'Salón de Belleza Aremi en la gestión de citas, el registro de servicios realizados y la '
    'liquidación de comisiones del personal?'
)

add_heading(doc, '2.3 Alcance del Proyecto y Delimitación', 2)
add_body(doc, 'El alcance de la solución informática abarca los siguientes módulos y capacidades funcionales:')
bullets_alcance = [
    'Autenticación de usuarios con roles diferenciados (Administradora / Empleada) mediante hash SHA-256.',
    'Gestión de clientes: registro, edición, búsqueda y eliminación lógica.',
    'Gestión de empleadas: registro, edición y asignación de porcentaje de comisión individual.',
    'Catálogo de servicios con tarifa de referencia (precio mutable independiente del historial).',
    'Agendamiento de citas con validación automática de conflictos de horario por empleada.',
    'Registro de servicios efectivamente realizados con congelamiento histórico del valor de comisión.',
    'Liquidación periódica de comisiones con procesamiento transaccional atómico (ACID).',
    'Emisión de comprobante PDF de liquidación con iText.',
    'Respaldo y restauración de base de datos mediante scripts SQL exportables.',
]
for b in bullets_alcance:
    add_bullet(doc, b)

add_body(doc,
    'El sistema no abarcará en este avance: comercio electrónico, aplicación móvil, ni integración '
    'con pasarelas de pago. El software opera exclusivamente en red local (LAN) o de forma local '
    'en el equipo de la administradora.'
)

# ══════════════════════════════════════════════════════════════════
# 3. JUSTIFICACIÓN
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '3. Justificación', 1)
add_heading(doc, '3.1 Beneficiarios Directos e Indirectos', 2)
add_body(doc, 'Los beneficiarios directos del sistema son:')
add_bullet(doc, 'La administradora del Salón Aremi: contará con una herramienta digital centralizada para controlar agenda, servicios y liquidaciones con trazabilidad completa.')
add_bullet(doc, 'Las empleadas del salón: recibirán liquidaciones de comisión transparentes, auditables y libres de errores de cálculo manual.')
add_body(doc,
    'Como beneficiarios indirectos se identifican las clientas del salón, quienes recibirán '
    'una atención mejor organizada y sin conflictos de horario, y la UNIAJC como institución '
    'formadora que valida la aplicación práctica de sus currículos de ingeniería.'
)

add_heading(doc, '3.2 Impacto Esperado en los Procesos Operativos', 2)
add_body(doc,
    'Con la implementación del software se proyecta: reducir a 0 % la tasa de citas duplicadas '
    'o cruzadas en la agenda; eliminar los errores en el cálculo de comisiones al sustituir '
    'operaciones manuales por cálculos automatizados con congelamiento histórico; reducir el tiempo '
    'de liquidación quincenal de aproximadamente dos horas a menos de cinco minutos; y establecer '
    'un repositorio digital de historial de clientes y servicios que permita análisis operativo futuro.'
)

add_heading(doc, '3.3 Viabilidad Técnica, Operativa y Financiera', 2)
add_bullet(doc, 'Viabilidad Técnica: Alta. El software utiliza tecnologías de código abierto maduras (Java 17, MySQL 8, iText 5.5), ampliamente documentadas y sin costo de licenciamiento.')
add_bullet(doc, 'Viabilidad Operativa: Alta. La interfaz Swing es intuitiva y no requiere conocimientos de informática avanzados en las usuarias finales.')
add_bullet(doc, 'Viabilidad Financiera: Alta. El único costo asociado es el tiempo de desarrollo del equipo académico. No hay costos de servidores externos ni licencias de software.')

add_heading(doc, '3.4 Justificación Académica e Institucional', 2)
add_body(doc,
    'Desde la perspectiva formativa de la UNIAJC, el proyecto permite articular integralmente '
    'los conocimientos de Ingeniería de Software I (análisis de requerimientos, diseño UML, '
    'metodologías ágiles e incrementales) con los de Programación II (arquitectura MVC, patrones '
    'de diseño, conexión a base de datos con JDBC y buenas prácticas de código limpio). '
    'El proyecto evidencia competencias de trabajo en equipo, control de versiones distribuido '
    '(Git), documentación técnica y presentación académica.'
)

# ══════════════════════════════════════════════════════════════════
# 4. OBJETIVOS
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '4. Objetivos', 1)
add_heading(doc, '4.1 Objetivo General', 2)
add_body(doc,
    'Desarrollar un sistema de información de escritorio bajo la arquitectura Modelo-Vista-Controlador '
    '(MVC) con el patrón DAO y base de datos MySQL, que automatice los procesos de gestión de citas, '
    'registro de servicios realizados y liquidación de comisiones del personal del Salón de Belleza '
    'Aremi, garantizando integridad de los datos, trazabilidad histórica y emisión de comprobantes PDF.'
)

add_heading(doc, '4.2 Objetivos Específicos', 2)
oe = [
    'Especificar diez requerimientos funcionales verificables con criterios de aceptación individuales en formato Gherkin.',
    'Diseñar el modelo relacional de la base de datos y el diagrama de clases UML del dominio real del negocio.',
    'Implementar el módulo de agendamiento de citas con validación de conflictos de horario por empleada.',
    'Desarrollar el motor de cálculo de comisiones con congelamiento histórico de porcentajes para garantizar inmutabilidad de liquidaciones pasadas.',
    'Integrar la generación de comprobantes PDF de liquidación mediante la biblioteca iText.',
    'Refactorizar el código fuente a la arquitectura MVC+DAO, eliminando consultas SQL de las vistas y centralizando la conexión en un Singleton con credenciales externas al código fuente.',
    'Documentar el proceso de desarrollo conforme a las guías académicas de la UNIAJC y el estilo APA 7.',
]
for o in oe:
    add_bullet(doc, o)

# ══════════════════════════════════════════════════════════════════
# 5. MARCO REFERENCIAL
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '5. Marco Referencial', 1)
add_heading(doc, '5.1 Marco Teórico', 2)
add_body(doc,
    'El patrón Modelo-Vista-Controlador (MVC) es un principio de diseño arquitectónico que separa '
    'una aplicación en tres componentes interconectados: el Modelo gestiona los datos y la lógica '
    'de negocio; la Vista presenta la información al usuario; y el Controlador actúa como intermediario '
    'entre ambos. Pressman y Maxim (2020) señalan que MVC reduce el acoplamiento entre la interfaz '
    'gráfica y la lógica de dominio, facilitando el mantenimiento evolutivo y las pruebas unitarias. '
    'Complementariamente, el patrón DAO (Data Access Object), descrito por Gamma et al. (1994), '
    'encapsula toda la lógica de acceso a datos en objetos especializados, permitiendo sustituir '
    'la tecnología de persistencia sin modificar las capas superiores del sistema.'
)
add_body(doc,
    'El patrón Singleton, aplicado en la clase ConexionBD, garantiza que exista una única instancia '
    'del objeto de conexión a la base de datos durante el ciclo de vida de la aplicación, '
    'optimizando recursos y previniendo fugas de conexión (Freeman y Robson, 2020). '
    'La criptografía SHA-256 (Stallings, 2018) se emplea para el hash unidireccional de '
    'contraseñas, evitando almacenar credenciales en texto plano. El procesamiento transaccional '
    'ACID (Atomicity, Consistency, Isolation, Durability) en la liquidación garantiza que si falla '
    'cualquier operación parcial, se realiza rollback completo (Elmasri y Navathe, 2017).'
)

add_heading(doc, '5.2 Marco Conceptual', 2)
conceptos = [
    ('Cita', 'Reserva temporal de la disponibilidad de una empleada para prestar un servicio específico a una clienta en una fecha y hora determinadas.'),
    ('Servicio Realizado', 'Certificación del cumplimiento efectivo de un servicio. Congela el valor de comisión histórico al momento de la prestación.'),
    ('Liquidación de Comisión', 'Proceso periódico de cálculo, agrupación y formalización del pago de las comisiones adeudadas a una empleada por los servicios realizados en un período.'),
    ('DAO (Data Access Object)', 'Clase cuya única responsabilidad es ejecutar operaciones CRUD contra la base de datos para una entidad específica del dominio.'),
    ('Singleton', 'Patrón creacional que restringe la instanciación de una clase a un único objeto, compartido globalmente en la aplicación.'),
    ('SHA-256', 'Función de hash criptográfico unidireccional de 256 bits utilizada para almacenar contraseñas de forma segura.'),
    ('RBAC', 'Control de Acceso Basado en Roles: modelo que asigna permisos a roles y luego roles a usuarios, restringiendo las operaciones disponibles según el perfil del actor autenticado.'),
]
for term, defi in conceptos:
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(4)
    r1 = p.add_run(f'{term}: ')
    r1.bold = True; r1.font.size = Pt(12); r1.font.name = 'Times New Roman'
    r2 = p.add_run(defi)
    r2.font.size = Pt(12); r2.font.name = 'Times New Roman'

add_heading(doc, '5.3 Marco Legal y Normativo', 2)
add_body(doc,
    'El sistema maneja datos personales de clientas y empleadas, por lo que debe ajustarse a '
    'la Ley Estatutaria 1581 de 2012 (Ley de Habeas Data en Colombia), que regula el tratamiento '
    'de datos personales. El software desarrollado en este proyecto es de uso interno y los datos '
    'no se comparten con terceros. Adicionalmente, la Ley 603 de 2000 establece la obligación '
    'de las empresas de reportar el uso de software debidamente licenciado. Dado que el proyecto '
    'usa exclusivamente tecnologías de código abierto (Java OpenJDK, MySQL Community, iText AGPL), '
    'se cumple este requisito sin costo.'
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 6. DISEÑO METODOLÓGICO
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '6. Diseño Metodológico: Desarrollo Incremental', 1)
add_heading(doc, '6.1 Metodología de Desarrollo Incremental', 2)
add_body(doc,
    'El proyecto adopta la metodología de desarrollo incremental propuesta por Sommerville (2019), '
    'que divide el desarrollo del software en una secuencia de incrementos funcionales entregables. '
    'Cada incremento entrega un subconjunto funcional del sistema final, es validado con los '
    'interesados y sirve de base para el incremento siguiente. Esta metodología es especialmente '
    'apropiada para proyectos con requerimientos evolutivos y equipos pequeños, como es el caso '
    'del Salón de Belleza Aremi.'
)

add_heading(doc, '6.2 Cronograma Temporal y Distribución de los Cinco Incrementos', 2)
add_table_caption(doc, 1, 'Cronograma de los cinco incrementos funcionales del proyecto. Elaboración propia.')

cols_cron = ['Incremento', 'Período', 'Módulos Desarrollados', 'Responsable Principal', 'Entregable']
table_cron = doc.add_table(rows=6, cols=5)
table_cron.style = 'Table Grid'
header_row(table_cron, cols_cron)
rows_cron = [
    ['I1', 'Sep 1–15, 2026', 'Autenticación, gestión de clientes y empleadas, catálogo de servicios', 'Yeneicy Michileno', 'Login funcional, CRUD clientes/empleadas/servicios en BD'],
    ['I2', 'Sep 16–30, 2026', 'Agendamiento de citas con validación de horarios, consulta y cancelación', 'Erick Enríquez', 'Módulo de agenda con detección de cruces y cambio de estado'],
    ['I3', 'Oct 1–15, 2026', 'Registro de servicios realizados, congelamiento histórico de comisión', 'Jeram Salazar', 'Módulo ServicioRealizado con lógica de precio histórico'],
    ['I4', 'Oct 16–31, 2026', 'Liquidación de comisiones, procesamiento ACID, emisión PDF', 'Jeram Salazar', 'Módulo de liquidación con comprobante PDF generado por iText'],
    ['I5', 'Nov 1–15, 2026', 'Integración, pruebas de sistema, refactorización MVC+DAO, documentación final', 'Todo el equipo', 'Sistema integrado, repositorio limpio, documento de sustentación'],
]
for i, row_data in enumerate(rows_cron, 1):
    bg = "F0F4F8" if i % 2 == 0 else None
    data_row(table_cron, i, row_data, bg)

add_note(doc, 'Las fechas son aproximadas y se ajustaron conforme a las retroalimentaciones del docente recibidas en cada entrega.')

add_heading(doc, '6.3 Entregables Concretos por Incremento', 2)
add_body(doc,
    'Cada incremento tiene asociados entregables concretos verificables. El Incremento 1 entrega '
    'las clases del paquete model (Cliente, Empleado, Servicio, Usuario) y los DAO correspondientes, '
    'con la pantalla de login y los formularios CRUD básicos. El Incremento 2 entrega la clase '
    'CitaDAO con el método existeCruceHorario() y la vista AgendarCitaGUI. El Incremento 3 entrega '
    'ServicioRealizadoDAO con congelamiento histórico. El Incremento 4 entrega LiquidacionDAO con '
    'transacción ACID y ReportePDFGenerator. El Incremento 5 entrega el repositorio GitHub limpio, '
    'sin archivos .class, con arquitectura MVC+DAO completa y este documento académico.'
)

add_heading(doc, '6.4 Matriz de Responsables del Equipo', 2)
add_table_caption(doc, 2, 'Distribución de responsabilidades por integrante del equipo. Elaboración propia.')
cols_resp = ['Integrante', 'Rol Principal', 'Paquetes / Clases a cargo', 'Correo institucional']
table_resp = doc.add_table(rows=4, cols=4)
table_resp.style = 'Table Grid'
header_row(table_resp, cols_resp)
rows_resp = [
    ['Erick Johan Enríquez Vera', 'Desarrollador — Módulo Agenda', 'controller/CitaController, dao/CitaDAO, view/AgendarCitaGUI, model/Cita', 'ejenriquez@estudiante.uniajc.edu.co'],
    ['Jeram Jeamdro Salazar Calderón', 'Desarrollador — Finanzas y PDF', 'controller/LiquidacionController, dao/LiquidacionDAO, dao/ServicioRealizadoDAO, util/ReportePDFGenerator', 'jsalazar@estudiante.uniajc.edu.co'],
    ['Yeneicy Alexandra Michileno Salcedo', 'Desarrolladora — Persistencia y Seguridad', 'dao/ConexionBD (Singleton), dao/ClienteDAO, dao/EmpleadoDAO, util/SeguridadManager, controller/AuthController', 'yamichileno@estudiante.uniajc.edu.co'],
]
for i, row_data in enumerate(rows_resp, 1):
    bg = "F0F4F8" if i % 2 == 0 else None
    data_row(table_resp, i, row_data, bg)

add_heading(doc, '6.5 Diferenciación entre Validaciones Planeadas y Pruebas Ejecutadas', 2)
add_body(doc,
    'Es importante distinguir entre las pruebas planeadas (casos de prueba diseñados en el '
    'documento pero aún no ejecutados) y las pruebas ejecutadas (realizadas sobre el código '
    'durante el desarrollo del incremento). En este segundo avance, las pruebas ejecutadas '
    'corresponden a pruebas unitarias manuales sobre los módulos de autenticación (SHA-256), '
    'validación de conflictos de horario (CitaDAO.existeCruceHorario()) y el rollback transaccional '
    'de la liquidación (verificado provocando un error forzado en la segunda inserción). '
    'Las pruebas de integración del sistema completo y las pruebas de aceptación con usuarios '
    'reales están planificadas para el Incremento 5.'
)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 7. REQUERIMIENTOS
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '7. Requerimientos del Sistema', 1)
add_heading(doc, '7.1 Requerimientos Funcionales (RF-01 a RF-10 con Criterios de Aceptación)', 2)

rfs = [
    {
        'id': 'RF-01', 'titulo': 'Autenticación de Usuarios con Control de Roles',
        'desc': 'El sistema debe autenticar a los usuarios mediante un nombre de usuario y una contraseña. '
                'Las contraseñas se almacenan como hash SHA-256. Tras la autenticación exitosa, el sistema '
                'despliega un menú diferenciado según el rol asignado (Administradora o Empleada).',
        'entradas': 'nombre_usuario (cadena ≤ 50 caracteres), contraseña (cadena 8–64 caracteres).',
        'salidas': 'Sesión activa con rol asignado; menú principal adaptado al perfil.',
        'validaciones': [
            'Si usuario o contraseña están vacíos: mostrar "Complete todos los campos requeridos."',
            'Si la contraseña tiene menos de 8 caracteres: mostrar "La contraseña debe tener al menos 8 caracteres."',
            'Si el hash no coincide con la BD: mostrar "Credenciales incorrectas. Intente nuevamente."',
            'Bloquear el campo contraseña con caracteres ocultos (•).',
            'Mantener sesión activa mientras la ventana principal esté abierta.',
        ],
        'gherkin': (
            'Dado que la administradora ingresa su usuario y contraseña correctos,\n'
            'cuando hace clic en "Iniciar sesión",\n'
            'entonces el sistema despliega el menú completo con las opciones de gestión de clientes,\n'
            'empleadas, agenda, servicios realizados y liquidación.\n\n'
            'Dado que una empleada ingresa credenciales válidas,\n'
            'cuando hace clic en "Iniciar sesión",\n'
            'entonces el sistema despliega solo las opciones de agenda y consulta de sus servicios.'
        ),
    },
    {
        'id': 'RF-02', 'titulo': 'Gestión Integral de Clientes (CRUD)',
        'desc': 'El sistema debe permitir el registro, consulta, edición y eliminación lógica de los '
                'datos de las clientas. La eliminación es lógica (campo activo = false) para preservar '
                'el historial de servicios asociados.',
        'entradas': 'Nombre completo (obligatorio), documento de identidad, teléfono, correo (opcional), dirección (opcional).',
        'salidas': 'Lista de clientes activos filtrada; confirmación de operación.',
        'validaciones': [
            'Nombre obligatorio; máximo 100 caracteres.',
            'Documento de identidad: solo numérico, 7–10 dígitos, único en la BD.',
            'Teléfono: formato numérico, 7–10 dígitos.',
            'Correo electrónico: formato válido (contiene @ y dominio) o vacío.',
            'No se puede eliminar físicamente un cliente si tiene citas asociadas: eliminar en modo lógico.',
        ],
        'gherkin': (
            'Dado que la administradora ingresa el nombre y el documento de un nuevo cliente,\n'
            'cuando hace clic en "Guardar",\n'
            'entonces el sistema registra el cliente en la base de datos y lo muestra en la lista.\n\n'
            'Dado que el documento de identidad ya existe en la BD,\n'
            'cuando se intenta guardar el registro,\n'
            'entonces el sistema muestra "Ya existe un cliente con ese número de documento."'
        ),
    },
    {
        'id': 'RF-03', 'titulo': 'Gestión de Empleadas con Porcentaje de Comisión Individual',
        'desc': 'El sistema debe permitir registrar y administrar los datos de las empleadas, incluyendo '
                'el porcentaje de comisión individual de cada una. El porcentaje se guarda al momento '
                'de crear o editar la empleada, pero los servicios realizados congelan el porcentaje '
                'vigente en su fecha de prestación para preservar la inmutabilidad histórica.',
        'entradas': 'Nombre completo (obligatorio), especialidad, porcentaje de comisión (0–100 %).',
        'salidas': 'Lista de empleadas activas; confirmación de operación.',
        'validaciones': [
            'Porcentaje de comisión: valor decimal entre 0.00 y 100.00; campo obligatorio.',
            'Especialidad: selección de lista o texto libre hasta 100 caracteres.',
            'El cambio del porcentaje de comisión NO modifica liquidaciones históricas ya procesadas.',
            'Una empleada con varias especialidades registra un único porcentaje general de comisión.',
        ],
        'gherkin': (
            'Dado que la administradora cambia el porcentaje de comisión de Monika del 30 % al 35 %,\n'
            'cuando guarda el cambio,\n'
            'entonces los nuevos servicios realizados usarán 35 %, pero las liquidaciones anteriores\n'
            'conservan el 30 % congelado en su momento de registro.'
        ),
    },
    {
        'id': 'RF-04', 'titulo': 'Catálogo de Servicios y Gestión de Tarifas',
        'desc': 'El sistema debe gestionar el catálogo de servicios del salón con nombre, descripción '
                'y precio de referencia. El precio es editable; su actualización no afecta servicios '
                'realizados pasados (precio congelado en servicio_realizado.valor_cobrado).',
        'entradas': 'Nombre del servicio (obligatorio), descripción, precio de referencia (> 0).',
        'salidas': 'Lista de servicios activos con precio vigente.',
        'validaciones': [
            'Precio de referencia: numérico positivo, mayor a 0.',
            'Nombre del servicio: único en la BD, máximo 100 caracteres.',
            'No se permite eliminar físicamente un servicio con servicios realizados asociados.',
        ],
        'gherkin': (
            'Dado que la administradora registra el servicio "Manicure" con precio $25.000,\n'
            'cuando lo guarda,\n'
            'entonces aparece en el catálogo y puede ser seleccionado al agendar citas.'
        ),
    },
    {
        'id': 'RF-05', 'titulo': 'Agendamiento de Citas con Validación de Disponibilidad',
        'desc': 'El sistema debe permitir registrar citas para clientas con una empleada, '
                'servicio y horario específicos. Antes de confirmar, el sistema debe validar que '
                'la empleada seleccionada no tenga otra cita solapada en el mismo rango de tiempo.',
        'entradas': 'ID clienta, ID empleada, ID servicio, fecha (YYYY-MM-DD), hora inicio (HH:MM), duración estimada (minutos).',
        'salidas': 'Cita registrada con estado "Pendiente"; mensaje de error si hay conflicto.',
        'validaciones': [
            'Fecha: no puede ser en el pasado.',
            'Hora inicio: entre 08:00 y 18:00.',
            'Duración: entre 15 y 240 minutos.',
            'Validación de cruce: si (hora_inicio_nueva < hora_fin_existente) Y (hora_fin_nueva > hora_inicio_existente) para la misma empleada → mostrar "La empleada ya tiene una cita en ese horario."',
            'Estado inicial siempre = "Pendiente".',
        ],
        'gherkin': (
            'Dado que Ashley tiene una cita de 10:00 a 11:00,\n'
            'cuando se intenta agendar otra cita para Ashley de 10:30 a 11:30,\n'
            'entonces el sistema muestra "La empleada ya tiene una cita en ese horario." y no registra la cita.\n\n'
            'Dado que se agenda una cita para Ashley de 11:00 a 12:00,\n'
            'cuando se guarda,\n'
            'entonces el sistema la registra exitosamente sin conflicto.'
        ),
    },
    {
        'id': 'RF-06', 'titulo': 'Consulta de Agenda Semanal por Empleada',
        'desc': 'El sistema debe mostrar las citas de una semana específica para una empleada '
                'seleccionada, con su estado (Pendiente, Atendida, Cancelada), cliente y servicio.',
        'entradas': 'ID empleada (opcional, si es vacío muestra todas), semana seleccionada.',
        'salidas': 'Tabla filtrada de citas con columnas: fecha, hora, cliente, servicio, estado.',
        'validaciones': [
            'Si no hay citas en el rango: mostrar "No hay citas registradas para los filtros seleccionados."',
            'El filtro por empleada es opcional; sin filtro muestra toda la agenda.',
        ],
        'gherkin': (
            'Dado que se selecciona a Ashley en el filtro de empleada y la semana del 10 al 16 de noviembre,\n'
            'cuando se hace clic en "Buscar",\n'
            'entonces el sistema muestra solo las citas de Ashley en esa semana con su estado.'
        ),
    },
    {
        'id': 'RF-07', 'titulo': 'Consulta y Cancelación de Citas con Liberación de Agenda',
        'desc': 'El sistema debe permitir cambiar el estado de una cita de "Pendiente" a "Cancelada", '
                'liberando el horario de la empleada para nuevas citas. Una cita "Atendida" o '
                '"Cancelada" no puede ser cancelada nuevamente.',
        'entradas': 'ID de cita a cancelar; confirmación del usuario.',
        'salidas': 'Estado de la cita actualizado a "Cancelada"; horario liberado.',
        'validaciones': [
            'Solo citas en estado "Pendiente" pueden cancelarse.',
            'Si se intenta cancelar una cita "Atendida": mostrar "No se puede cancelar una cita ya atendida."',
            'Si se intenta cancelar una cita "Cancelada": mostrar "Esta cita ya fue cancelada anteriormente."',
        ],
        'gherkin': (
            'Dado que una cita está en estado "Pendiente",\n'
            'cuando la administradora la selecciona y hace clic en "Cancelar cita",\n'
            'entonces el estado cambia a "Cancelada" y el horario queda disponible para nuevas citas.'
        ),
    },
    {
        'id': 'RF-08', 'titulo': 'Registro de Servicios Efectivamente Realizados',
        'desc': 'El sistema debe certificar la prestación real de un servicio, congelando el valor '
                'cobrado y el porcentaje de comisión vigente al momento del registro. '
                'El estado del servicio queda en "Pendiente" de liquidación.',
        'entradas': 'ID empleada, ID servicio, ID clienta, fecha de atención, valor cobrado, notas.',
        'salidas': 'Registro de servicio realizado con estado "Pendiente"; ID generado.',
        'validaciones': [
            'Valor cobrado: mayor a 0.',
            'El porcentaje de comisión se congela desde el perfil de la empleada en ese instante.',
            'Un servicio realizado no puede marcarse como "Liquidado" sin pasar por el proceso de liquidación formal.',
            'No se permite registrar el mismo servicio realizado dos veces para la misma cita (si se originó de una cita).',
        ],
        'gherkin': (
            'Dado que Ashley (comisión 30 %) prestó un servicio de Manicure por $25.000,\n'
            'cuando se registra el servicio realizado,\n'
            'entonces el sistema guarda valor_cobrado = 25000 y porcentaje_comision = 30,\n'
            'y el estado queda en "Pendiente" de liquidación.'
        ),
    },
    {
        'id': 'RF-09', 'titulo': 'Cálculo Automatizado de Liquidación de Comisiones',
        'desc': 'El sistema debe consultar los servicios en estado "Pendiente" de una empleada '
                'en un rango de fechas y calcular automáticamente el total de ventas, '
                'el monto de comisión y el saldo a pagar.',
        'entradas': 'ID empleada, fecha inicio, fecha fin.',
        'salidas': 'Tarjetas de resumen: total ventas, total comisión, número de servicios.',
        'validaciones': [
            'Fecha inicio ≤ fecha fin.',
            'Solo se procesan servicios en estado "Pendiente".',
            'Si no hay servicios en el rango: mostrar "No hay servicios pendientes de liquidar en el período seleccionado."',
            'Fórmula: comisión = valor_cobrado × (porcentaje_comision / 100), calculada por servicio y sumada.',
        ],
        'gherkin': (
            'Dado que Monika prestó 4 servicios por $120.000 con comisión del 30 %,\n'
            'cuando se calcula la liquidación del período,\n'
            'entonces el sistema muestra: Total ventas = $120.000, Total comisión = $36.000, Servicios = 4.'
        ),
    },
    {
        'id': 'RF-10', 'titulo': 'Procesamiento Atómico de Liquidación y Emisión de Comprobante PDF',
        'desc': 'El sistema debe asentar la liquidación mediante una transacción atómica ACID: '
                'inserta el registro en tabla liquidaciones, actualiza el estado de los servicios '
                'a "Liquidado" y genera el comprobante PDF. Si falla cualquier paso, se realiza '
                'rollback completo.',
        'entradas': 'Confirmación de la liquidación; notas u observaciones opcionales.',
        'salidas': 'Registro de liquidación en BD; comprobante PDF con número correlativo; estados de servicios actualizados a "Liquidado".',
        'validaciones': [
            'Transacción ACID: si falla la inserción o la actualización de servicios, se hace rollback total.',
            'El PDF se genera solo si la transacción fue exitosa.',
            'Los servicios marcados como "Liquidado" no vuelven a aparecer en futuros cálculos.',
            'El número de liquidación es consecutivo y único en la BD.',
        ],
        'gherkin': (
            'Dado que se aprueba la liquidación de Ashley,\n'
            'cuando se procesa,\n'
            'entonces el sistema: (1) inserta en tabla liquidaciones, '
            '(2) actualiza los servicios a "Liquidado", '
            '(3) genera el PDF y lo abre; '
            'si cualquier paso falla, ninguna operación queda en la BD.'
        ),
    },
]

for rf in rfs:
    p = doc.add_paragraph()
    r = p.add_run(f'{rf["id"]}: {rf["titulo"]}')
    r.bold = True; r.font.size = Pt(12); r.font.name = 'Times New Roman'
    p.paragraph_format.space_before = Pt(10)

    p2 = doc.add_paragraph()
    r2 = p2.add_run('Descripción: ')
    r2.bold = True; r2.font.size = Pt(12); r2.font.name = 'Times New Roman'
    r2b = p2.add_run(rf['desc'])
    r2b.font.size = Pt(12); r2b.font.name = 'Times New Roman'
    p2.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY

    p3 = doc.add_paragraph()
    r3 = p3.add_run(f'Entradas: ')
    r3.bold = True; r3.font.size = Pt(11); r3.font.name = 'Times New Roman'
    r3b = p3.add_run(rf['entradas'])
    r3b.font.size = Pt(11); r3b.font.name = 'Times New Roman'

    p4 = doc.add_paragraph()
    r4 = p4.add_run('Salidas: ')
    r4.bold = True; r4.font.size = Pt(11); r4.font.name = 'Times New Roman'
    r4b = p4.add_run(rf['salidas'])
    r4b.font.size = Pt(11); r4b.font.name = 'Times New Roman'

    p5 = doc.add_paragraph()
    r5 = p5.add_run('Validaciones y Reglas del Sistema:')
    r5.bold = True; r5.font.size = Pt(11); r5.font.name = 'Times New Roman'
    for v in rf['validaciones']:
        add_bullet(doc, v)

    p6 = doc.add_paragraph()
    r6 = p6.add_run('Criterio de Aceptación (Gherkin):')
    r6.bold = True; r6.font.size = Pt(11); r6.font.name = 'Times New Roman'
    add_monospace(doc, rf['gherkin'])

add_heading(doc, '7.2 Requerimientos No Funcionales (RNF-01 a RNF-07 con Métricas)', 2)
rnfs = [
    ('RNF-01: Seguridad Criptográfica de Credenciales y Gestión de Sesión',
     'Las contraseñas se almacenan exclusivamente como hash SHA-256 (256 bits). '
     'Las credenciales de BD se leen de un archivo db.properties externo, nunca hardcodeadas en el código fuente. '
     'La sesión activa se mantiene en memoria (UsuarioSesion) y se destruye al cerrar la ventana principal. '
     'Métrica: 0 contraseñas en texto plano en la BD; 0 credenciales de BD en archivos .java.'),
    ('RNF-02: Usabilidad y Experiencia de Usuario (UX/UI)',
     'La interfaz gráfica en Swing debe responder a todas las acciones del usuario en menos de 200 ms (retroalimentación visual). '
     'Los mensajes de error deben ser descriptivos e indicar la acción correctiva. '
     'Métrica: en prueba con 2 usuarios sin entrenamiento previo, deben completar el registro de una cita en menos de 3 minutos.'),
    ('RNF-03: Rendimiento y Eficiencia de Respuesta',
     'Todas las consultas de búsqueda (clientes, agenda semanal, cálculo de liquidación) deben ejecutarse en menos de 2 segundos '
     'con una base de datos de hasta 10.000 registros en hardware de gama media (procesador i5, 8 GB RAM, SSD). '
     'Métrica: tiempo de respuesta < 2 segundos en consultas sobre tabla con 10.000 filas.'),
    ('RNF-04: Integridad Referencial y Consistencia Transaccional',
     'La BD impone claves primarias, claves foráneas y restricciones CHECK. '
     'Las liquidaciones usan transacciones con autocommit=false y rollback en caso de error. '
     'Métrica: en 10 pruebas de liquidación con error forzado en la segunda operación, el 100 % debe hacer rollback sin datos inconsistentes.'),
    ('RNF-05: Control de Acceso Basado en Roles (RBAC)',
     'La administradora tiene acceso a todos los módulos. '
     'Las empleadas solo acceden a agenda y consulta de sus propios servicios. '
     'Ningún componente de la capa Vista accede directamente a la BD: toda operación pasa por el Controller y el DAO. '
     'Métrica: en prueba de penetración manual, una sesión de Empleada no puede invocar métodos de LiquidacionController ni ClienteController.'),
    ('RNF-06: Respaldo, Recuperación y Portabilidad de Datos',
     'El sistema provee la opción de exportar un volcado SQL de la BD mediante el script backup.bat (mysqldump). '
     'El archivo exportado debe poder restaurarse sin pérdida de datos. '
     'Métrica: restaurar un respaldo de la BD en un equipo limpio en menos de 5 minutos.'),
    ('RNF-07: Disponibilidad y Resiliencia Local',
     'El sistema opera sin conexión a Internet. '
     'Si la BD está temporalmente inaccesible, el sistema muestra el mensaje "No se pudo conectar a la base de datos. '
     'Verifique que MySQL esté en ejecución." y permite reintentar sin cerrar la aplicación. '
     'Métrica: el sistema inicia y muestra la pantalla de login en menos de 3 segundos en hardware de gama media.'),
]
for titulo, desc in rnfs:
    p = doc.add_paragraph()
    r = p.add_run(titulo)
    r.bold = True; r.font.size = Pt(12); r.font.name = 'Times New Roman'
    p.paragraph_format.space_before = Pt(8)
    add_body(doc, desc)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 8. CASOS DE USO
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '8. Casos de Uso y Matriz de Roles', 1)
add_heading(doc, '8.1 Matriz de Roles y Permisos (RBAC)', 2)
add_body(doc, 'La Tabla 3 describe los permisos asignados a cada uno de los roles autorizados del sistema:')
add_table_caption(doc, 3, 'Matriz de control de acceso basado en roles (RBAC). Elaboración propia.')

cols_rbac = ['Función del Sistema', 'Administradora', 'Empleada']
table_rbac = doc.add_table(rows=12, cols=3)
table_rbac.style = 'Table Grid'
header_row(table_rbac, cols_rbac)
rows_rbac = [
    ['Iniciar sesión', '✓', '✓'],
    ['Gestionar clientes (CRUD)', '✓', '✗'],
    ['Gestionar empleadas (CRUD)', '✓', '✗'],
    ['Gestionar catálogo de servicios', '✓', '✗'],
    ['Agendar citas para cualquier empleada', '✓', '✗'],
    ['Consultar agenda propia', '✓', '✓'],
    ['Cancelar citas', '✓', '✗'],
    ['Registrar servicio realizado', '✓', '✓'],
    ['Consultar servicios propios realizados', '✓', '✓'],
    ['Calcular y procesar liquidación', '✓', '✗'],
    ['Generar comprobante PDF', '✓', '✗'],
]
for i, row_data in enumerate(rows_rbac, 1):
    bg = "F0F4F8" if i % 2 == 0 else None
    data_row(table_rbac, i, row_data, bg)

add_note(doc, 'Las empleadas solo visualizan su propia agenda y sus propios servicios realizados. No tienen acceso a información de otras empleadas ni a funciones financieras.')

add_heading(doc, '8.2 Especificación de Casos de Uso Principales (CU-01 a CU-08)', 2)
cus = [
    ('CU-01: Iniciar Sesión', 'Administradora / Empleada', 'Acceder al sistema con credenciales válidas.',
     '1. Actor ingresa nombre de usuario y contraseña.\n2. Sistema calcula hash SHA-256 de la contraseña.\n3. Sistema consulta la BD con usuario y hash.\n4. Si coincide: crea sesión y despliega menú según rol.\n5. Si no coincide: muestra "Credenciales incorrectas."'),
    ('CU-02: Registrar Cliente', 'Administradora', 'Agregar una nueva clienta al sistema.',
     '1. Administradora selecciona "Nuevo cliente".\n2. Ingresa nombre, documento, teléfono.\n3. Sistema valida unicidad del documento.\n4. Sistema guarda el registro y lo muestra en la lista.'),
    ('CU-03: Gestionar Empleadas', 'Administradora', 'Registrar y editar datos de empleadas incluyendo porcentaje de comisión.',
     '1. Administradora selecciona "Empleadas".\n2. Registra nombre, especialidad, porcentaje de comisión.\n3. Sistema valida el porcentaje (0–100 %).\n4. Sistema guarda el registro.'),
    ('CU-04: Agendar Cita', 'Administradora', 'Reservar un horario para una clienta con una empleada.',
     '1. Administradora selecciona clienta, empleada, servicio, fecha y hora.\n2. Sistema verifica disponibilidad: llama a CitaDAO.existeCruceHorario().\n3. Si hay cruce: muestra mensaje de error.\n4. Si está libre: registra cita con estado "Pendiente".'),
    ('CU-05: Cancelar Cita', 'Administradora', 'Cambiar estado de cita "Pendiente" a "Cancelada".',
     '1. Administradora selecciona cita en estado "Pendiente".\n2. Confirma cancelación.\n3. Sistema actualiza estado a "Cancelada".\n4. Horario queda disponible para nuevas citas.'),
    ('CU-06: Registrar Servicio Realizado', 'Administradora / Empleada', 'Certificar la prestación efectiva de un servicio.',
     '1. Actor ingresa empleada, servicio, clienta, valor cobrado.\n2. Sistema congela porcentaje de comisión vigente de la empleada.\n3. Sistema registra el servicio realizado con estado "Pendiente".\n4. Sistema muestra confirmación con ID generado.'),
    ('CU-07: Calcular Liquidación', 'Administradora', 'Calcular el monto de comisión adeudado a una empleada en un período.',
     '1. Administradora selecciona empleada, fecha inicio, fecha fin.\n2. Sistema consulta servicios realizados en estado "Pendiente" del período.\n3. Sistema muestra: total ventas, total comisión, número de servicios.\n4. Administradora decide si procede a formalizar la liquidación.'),
    ('CU-08: Procesar Liquidación y Generar PDF', 'Administradora', 'Formalizar el pago de comisión y emitir comprobante.',
     '1. Administradora confirma la liquidación.\n2. Sistema inicia transacción:\n   a. Inserta registro en tabla liquidaciones.\n   b. Actualiza servicios a estado "Liquidado".\n3. Si todo exitoso: commit y genera PDF.\n4. Si falla: rollback total; muestra "Error al procesar la liquidación. No se realizaron cambios."'),
]
for cu_id, actor, objetivo, flujo in cus:
    p = doc.add_paragraph()
    r = p.add_run(cu_id)
    r.bold = True; r.font.size = Pt(12); r.font.name = 'Times New Roman'
    p.paragraph_format.space_before = Pt(8)

    p2 = doc.add_paragraph()
    r2a = p2.add_run('Actor: ')
    r2a.bold = True; r2a.font.size = Pt(11); r2a.font.name = 'Times New Roman'
    r2b = p2.add_run(f'{actor}  |  ')
    r2b.font.size = Pt(11); r2b.font.name = 'Times New Roman'
    r2c = p2.add_run('Objetivo: ')
    r2c.bold = True; r2c.font.size = Pt(11); r2c.font.name = 'Times New Roman'
    r2d = p2.add_run(objetivo)
    r2d.font.size = Pt(11); r2d.font.name = 'Times New Roman'

    p3 = doc.add_paragraph()
    r3 = p3.add_run('Flujo Principal:')
    r3.bold = True; r3.font.size = Pt(11); r3.font.name = 'Times New Roman'
    add_monospace(doc, flujo)

add_heading(doc, '8.3 Diagrama General de Casos de Uso (UML)', 2)
add_body(doc,
    'La Figura 1 representa el diagrama de casos de uso del sistema, mostrando los dos actores '
    'principales (Administradora y Empleada) y las funcionalidades del sistema con sus relaciones '
    'de inclusión (<<include>>) y extensión (<<extend>>). Se presenta en notación ASCII por '
    'compatibilidad con el documento; el diagrama UML formal se encuentra en el repositorio '
    'del proyecto (ver DOCUMENTACION_ARQUITECTURA_Y_SUSTENTACION.md).'
)

diagrama_cu = """\
╔══════════════════════════════════════════════════════════════════╗
║              SISTEMA DE GESTIÓN - SALÓN AREMI                   ║
║                                                                  ║
║  ┌──────────────────────────────────────────────────────────┐   ║
║  │                                                          │   ║
║  │   (Iniciar Sesión)─────────────────────────────────┐    │   ║
║  │                                                     │    │   ║
║  │   (Gestionar Clientes)                              │    │   ║
║  │   (Gestionar Empleadas)      «include»              │    │   ║
║  │   (Gestionar Servicios)─────(Validar Autenticación) │    │   ║
║  │   (Agendar Cita)─────────────────────────────────┘  │    │   ║
║  │      └──«include»──(Verificar Disponibilidad)        │    │   ║
║  │   (Cancelar Cita)                                    │    │   ║
║  │   (Registrar Servicio Realizado)                     │    │   ║
║  │   (Calcular Liquidación)                             │    │   ║
║  │   (Procesar Liquidación)                             │    │   ║
║  │      └──«include»──(Generar PDF)                     │    │   ║
║  │   (Consultar Agenda)                                 │    │   ║
║  │                                                          │   ║
║  └──────────────────────────────────────────────────────────┘   ║
║                                                                  ║
║  [Administradora]──────────────────────────────────────────────  ║
║     Todos los casos de uso                                       ║
║                                                                  ║
║  [Empleada]────────────────────────────────────────────────────  ║
║     (Iniciar Sesión), (Consultar Agenda), (Registrar Servicio)   ║
╚══════════════════════════════════════════════════════════════════╝"""

add_monospace(doc, diagrama_cu)
add_figure_caption(doc, 1, 'Diagrama de casos de uso del sistema de gestión Salón Aremi. Elaboración propia.')

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 9. DIAGRAMAS DE ACTIVIDADES
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '9. Diagramas de Actividades', 1)
add_heading(doc, '9.1 Proceso de Agendamiento de Citas con Validación de Horarios', 2)
add_body(doc,
    'La Figura 2 representa el flujo de actividades del proceso de agendamiento de una cita, '
    'incluyendo la validación de disponibilidad de la empleada. Este proceso implementa el '
    'requisito RF-05.'
)

actividad_cita = """\
  ┌────────────────────────────────────────────────────────────────┐
  │   ADMINISTRADORA              │   SISTEMA                      │
  ├───────────────────────────────┼────────────────────────────────┤
  │                               │                                │
  │  [Inicio]                     │                                │
  │       │                       │                                │
  │  Selecciona: clienta,         │                                │
  │  empleada, servicio,          │                                │
  │  fecha y hora inicio          │                                │
  │       │                       │                                │
  │  Ingresa duración estimada ──►│ Valida campos obligatorios     │
  │                               │ (fecha futura, hora válida)    │
  │                               │       │                        │
  │                               │   ┌───▼────────────────────┐   │
  │                               │   │ Consulta BD: ¿existe   │   │
  │                               │   │ cruce de horario para  │   │
  │                               │   │ la empleada?           │   │
  │                               │   └───────────┬────────────┘   │
  │                               │          Sí   │   No           │
  │                               │          ▼    │    ▼           │
  │  ◄─ Muestra error:            │  Error   │    │  Registra cita │
  │  "Empleada no disponible"     │  mostrado│    │  estado=Pend.  │
  │                               │          │    │       │        │
  │  Corrige horario o empleada   │          │    │  Muestra conf. │
  │       │                       │          │    │  "Cita agendada│
  │       └───────────────────────┘          │    │  exitosamente" │
  │                                          │    │                │
  │                                      [Fin]  [Fin]             │
  └────────────────────────────────────────────────────────────────┘"""

add_monospace(doc, actividad_cita)
add_figure_caption(doc, 2, 'Diagrama de actividades: proceso de agendamiento de citas con validación de disponibilidad. Elaboración propia.')

add_heading(doc, '9.2 Proceso de Atención, Prestación de Servicio y Liquidación de Comisiones', 2)
add_body(doc,
    'La Figura 3 representa el flujo desde la atención de la clienta hasta la liquidación '
    'formal de la comisión de la empleada, incluyendo la generación del comprobante PDF. '
    'Este proceso implementa los requerimientos RF-08, RF-09 y RF-10.'
)

actividad_liquid = """\
  ┌────────────────────────────────────────────────────────────────┐
  │  ADMINISTRADORA / EMPLEADA    │   SISTEMA                      │
  ├───────────────────────────────┼────────────────────────────────┤
  │                               │                                │
  │  [Inicio: Clienta atendida]   │                                │
  │       │                       │                                │
  │  Registra Servicio Realizado  │                                │
  │  (empleada, servicio, valor)──►│ Congela porcentaje comisión   │
  │                               │ de la empleada (snapshot)      │
  │                               │ Guarda: estado = "Pendiente"   │
  │                               │                                │
  │       ─ ─ ─ ─ PERÍODO QUINCENA ─ ─ ─ ─                        │
  │                               │                                │
  │  Selecciona empleada +        │                                │
  │  rango de fechas ─────────── ►│ Consulta servicios "Pendiente" │
  │                               │ Calcula: Σ(valor × comisión%)  │
  │  ◄─────────────────────────── │ Muestra tarjetas resumen       │
  │                               │                                │
  │  Revisa y confirma ──────────►│ Inicia transacción ACID        │
  │                               │   ┌─► INSERT liquidaciones     │
  │                               │   │   UPDATE servicios         │
  │                               │   │   = "Liquidado"            │
  │                               │   │                            │
  │                               │ ¿Éxito?                        │
  │                               │   Sí ──► COMMIT ──► Genera PDF │
  │  ◄─────────────── PDF abierto─┤   No ──► ROLLBACK             │
  │                               │          Muestra error         │
  │  [Fin]                        │  [Fin]                         │
  └────────────────────────────────────────────────────────────────┘"""

add_monospace(doc, actividad_liquid)
add_figure_caption(doc, 3, 'Diagrama de actividades: proceso de liquidación de comisiones con transacción ACID. Elaboración propia.')

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 10. DISEÑO TÉCNICO
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '10. Diseño Técnico y Arquitectónico del Sistema', 1)
add_heading(doc, '10.1 Arquitectura Modelo-Vista-Controlador (MVC) y Patrón DAO', 2)
add_body(doc,
    'A solicitud de la retroalimentación docente, se eliminó completamente la práctica de '
    'vincular consultas SQL directamente a los componentes de la capa Vista. '
    'La arquitectura refactorizada se organiza en cuatro paquetes funcionales:'
)
add_bullet(doc, 'model: clases POJO del dominio (Cliente, Empleado, Servicio, Cita, ServicioRealizado, LiquidacionComision, Usuario). Sin lógica de BD, sin referencias a Swing.')
add_bullet(doc, 'dao: una clase por entidad (ClienteDAO, EmpleadoDAO, ServicioDAO, CitaDAO, ServicioRealizadoDAO, LiquidacionDAO, UsuarioDAO) + ConexionBD (Singleton). Solo SQL, PreparedStatement, ResultSet.')
add_bullet(doc, 'controller: orquesta la lógica de negocio entre la Vista y el DAO. Valida reglas (cruces de horario, transacciones ACID). No tiene SQL ni componentes gráficos.')
add_bullet(doc, 'view: formularios Swing. Solo llama métodos del Controller. No tiene SQL ni instancia DAOs directamente.')
add_bullet(doc, 'util: clases utilitarias transversales (SeguridadManager para SHA-256, ReportePDFGenerator para iText).')

add_body(doc,
    'La conexión a la base de datos se centraliza en ConexionBD.java (Singleton), que lee '
    'las credenciales del archivo db.properties (excluido del repositorio por .gitignore). '
    'Se provee db.properties.example como plantilla para configuración inicial. '
    'Esto elimina el hallazgo crítico de la contraseña "admin1023" hardcodeada en DatabaseConfig.java.'
)

add_heading(doc, '10.2 Diagrama de Clases UML del Dominio Real', 2)
add_body(doc,
    'La Figura 4 presenta el diagrama de clases simplificado del dominio. Incluye las entidades '
    'ServicioRealizado y LiquidacionComision que faltaban en el diagrama original, '
    'atendiendo directamente la observación del docente.'
)

diagrama_clases = """\
 ┌──────────────────┐          ┌──────────────────────┐
 │   <<model>>      │          │   <<model>>          │
 │   Cliente        │1       N │   Cita               │
 │──────────────────│◄─────────│──────────────────────│
 │ -id: int         │          │ -id: int             │
 │ -nombre: String  │          │ -fechaHora: LocalDT  │
 │ -documento: Str  │          │ -duracionMin: int    │
 │ -telefono: Str   │          │ -estado: String      │
 │ -activo: boolean │          │ -clienteId: int      │
 └──────────────────┘          │ -empleadaId: int     │
                               │ -servicioId: int     │
 ┌──────────────────┐          └──────────────────────┘
 │   <<model>>      │                    │1
 │   Empleado       │1                   │
 │──────────────────│◄──┐    ┌───────────▼──────────────┐
 │ -id: int         │   │    │   <<model>>              │
 │ -nombre: String  │   │    │   ServicioRealizado      │
 │ -especialidad    │   │    │──────────────────────────│
 │ -porcComision    │   │    │ -id: int                 │
 │ -activo: boolean │   │    │ -empleadaId: int         │
 └──────────────────┘   │    │ -servicioId: int         │
         │1             └────│ -valorCobrado: double    │
         │                   │ -porcComisionHist: double│
 ┌───────▼──────────┐   N    │ -estado: String          │
 │   <<model>>      │        │ -fechaAtencion: LocalDate│
 │   LiquidacionCom.│        └──────────────────────────┘
 │──────────────────│
 │ -id: int         │  ┌──────────────────┐
 │ -empleadaId: int │  │   <<model>>      │
 │ -fechaInicio     │  │   Servicio       │
 │ -fechaFin        │  │──────────────────│
 │ -totalVentas     │  │ -id: int         │
 │ -totalComision   │  │ -nombre: String  │
 │ -notas: String   │  │ -descripcion     │
 └──────────────────┘  │ -precio: double  │
                       │ -activo: boolean │
                       └──────────────────┘"""

add_monospace(doc, diagrama_clases)
add_figure_caption(doc, 4, 'Diagrama de clases UML del dominio del sistema (paquete model). Las relaciones N:1 y 1:N representan las claves foráneas de la base de datos. Elaboración propia.')

add_heading(doc, '10.3 Modelo Entidad-Relación (MER) y Esquema de Base de Datos', 2)
add_body(doc,
    'La Tabla 4 presenta el esquema de las tablas de la base de datos relacional aremi_db, '
    'con sus columnas, tipos de datos, restricciones y relaciones de integridad referencial.'
)
add_table_caption(doc, 4, 'Esquema de la base de datos relacional aremi_db. Elaboración propia.')

cols_mer = ['Tabla', 'Columna', 'Tipo', 'Restricciones', 'Descripción']
table_mer = doc.add_table(rows=25, cols=5)
table_mer.style = 'Table Grid'
header_row(table_mer, cols_mer)

mer_rows = [
    # clientes
    ['clientes', 'id', 'INT', 'PK, AUTO_INCREMENT', 'Identificador único'],
    ['clientes', 'nombre', 'VARCHAR(100)', 'NOT NULL', 'Nombre completo'],
    ['clientes', 'documento', 'VARCHAR(15)', 'UNIQUE, NOT NULL', 'Nro. de identidad'],
    ['clientes', 'telefono', 'VARCHAR(15)', '', 'Teléfono de contacto'],
    ['clientes', 'activo', 'TINYINT(1)', 'DEFAULT 1', 'Eliminación lógica'],
    # empleados
    ['empleados', 'id', 'INT', 'PK, AUTO_INCREMENT', 'Identificador único'],
    ['empleados', 'nombre', 'VARCHAR(100)', 'NOT NULL', 'Nombre completo'],
    ['empleados', 'porcentaje_comision', 'DECIMAL(5,2)', 'NOT NULL', 'Comisión actual (%)'],
    # servicios
    ['servicios', 'id', 'INT', 'PK, AUTO_INCREMENT', 'Identificador único'],
    ['servicios', 'nombre', 'VARCHAR(100)', 'UNIQUE, NOT NULL', 'Nombre del servicio'],
    ['servicios', 'precio', 'DECIMAL(10,2)', 'NOT NULL, > 0', 'Tarifa de referencia'],
    # citas
    ['citas', 'id', 'INT', 'PK, AUTO_INCREMENT', 'Identificador único'],
    ['citas', 'cliente_id', 'INT', 'FK → clientes.id', 'Cliente de la cita'],
    ['citas', 'empleado_id', 'INT', 'FK → empleados.id', 'Empleada asignada'],
    ['citas', 'fecha_hora', 'DATETIME', 'NOT NULL', 'Inicio de la cita'],
    ['citas', 'estado', 'VARCHAR(20)', "DEFAULT 'Pendiente'", 'Pendiente/Atendida/Cancelada'],
    # servicios_realizados
    ['servicios_realizados', 'id', 'INT', 'PK, AUTO_INCREMENT', 'Identificador único'],
    ['servicios_realizados', 'empleado_id', 'INT', 'FK → empleados.id', 'Empleada que lo prestó'],
    ['servicios_realizados', 'valor_cobrado', 'DECIMAL(10,2)', 'NOT NULL', 'Precio congelado'],
    ['servicios_realizados', 'porc_comision_hist', 'DECIMAL(5,2)', 'NOT NULL', '% comisión histórico'],
    ['servicios_realizados', 'estado', 'VARCHAR(20)', "DEFAULT 'Pendiente'", 'Pendiente/Liquidado'],
    # liquidaciones
    ['liquidaciones', 'id', 'INT', 'PK, AUTO_INCREMENT', 'Identificador único'],
    ['liquidaciones', 'empleado_id', 'INT', 'FK → empleados.id', 'Empleada liquidada'],
    ['liquidaciones', 'total_comision', 'DECIMAL(10,2)', 'NOT NULL', 'Monto pagado'],
]
for i, row_data in enumerate(mer_rows, 1):
    bg = "F0F4F8" if i % 2 == 0 else None
    data_row(table_mer, i, row_data, bg)

add_note(doc, 'El script SQL completo de creación (DDL) se encuentra en el archivo schema.sql del repositorio del proyecto.')

add_heading(doc, '10.4 Reglas Matemáticas y Consulta SQL de Liquidación', 2)
add_body(doc, 'La comisión de un servicio realizado se calcula con la siguiente fórmula:')
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = p.add_run('comisión_servicio = valor_cobrado × (porcentaje_comision_historico / 100)')
run.font.name = 'Courier New'; run.font.size = Pt(11); run.bold = True

add_body(doc, 'Y la liquidación total para un período:')
p2 = doc.add_paragraph()
p2.alignment = WD_ALIGN_PARAGRAPH.CENTER
run2 = p2.add_run("total_comision = Σ(valor_cobrado_i × porcentaje_comision_historico_i / 100),  donde estado_i = 'Pendiente'")
run2.font.name = 'Courier New'; run2.font.size = Pt(11); run2.bold = True

add_body(doc, 'La consulta SQL correspondiente es:')
sql_text = """\
SELECT
    sr.id,
    sr.valor_cobrado,
    sr.porc_comision_hist,
    (sr.valor_cobrado * sr.porc_comision_hist / 100) AS comision_servicio
FROM servicios_realizados sr
WHERE sr.empleado_id = ?
  AND sr.fecha_atencion BETWEEN ? AND ?
  AND sr.estado = 'Pendiente';"""
add_monospace(doc, sql_text)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 11. PLAN DE PRUEBAS
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '11. Plan de Pruebas y Matriz de Verificación', 1)
add_body(doc,
    'La Tabla 5 presenta diez escenarios de prueba que verifican los requerimientos funcionales '
    'y las reglas de negocio críticas del sistema. Se diferencia entre pruebas planeadas (P) '
    'y ejecutadas (E).'
)
add_table_caption(doc, 5, 'Matriz de ejecución de pruebas funcionales y de negocio. Elaboración propia.')

cols_prueba = ['ID', 'RF', 'Descripción del Caso de Prueba', 'Resultado Esperado', 'Estado']
table_prueba = doc.add_table(rows=11, cols=5)
table_prueba.style = 'Table Grid'
header_row(table_prueba, cols_prueba)
prueba_rows = [
    ['PT-01', 'RF-01', 'Iniciar sesión con usuario y contraseña correctos.', 'Se despliega menú según rol.', 'E ✓'],
    ['PT-02', 'RF-01', 'Iniciar sesión con contraseña incorrecta.', 'Mensaje "Credenciales incorrectas."', 'E ✓'],
    ['PT-03', 'RF-02', 'Registrar cliente con documento duplicado.', 'Mensaje "Ya existe un cliente con ese documento."', 'E ✓'],
    ['PT-04', 'RF-05', 'Agendar cita con horario solapado en la misma empleada.', 'Mensaje "La empleada ya tiene una cita en ese horario." Sin registro.', 'E ✓'],
    ['PT-05', 'RF-05', 'Agendar cita con empleada disponible.', 'Cita registrada con estado "Pendiente".', 'E ✓'],
    ['PT-06', 'RF-07', 'Cancelar cita en estado "Atendida".', 'Mensaje "No se puede cancelar una cita ya atendida."', 'P'],
    ['PT-07', 'RF-08', 'Registrar servicio realizado; verificar congelamiento de comisión.', 'porc_comision_hist = valor vigente al momento del registro.', 'E ✓'],
    ['PT-08', 'RF-09', 'Calcular liquidación sin servicios pendientes en el período.', 'Mensaje "No hay servicios pendientes de liquidar."', 'P'],
    ['PT-09', 'RF-10', 'Procesar liquidación con error forzado en UPDATE de servicios.', 'Rollback total; sin registros en tabla liquidaciones.', 'E ✓'],
    ['PT-10', 'RF-10', 'Procesar liquidación exitosa; verificar PDF generado.', 'PDF guardado; servicios en estado "Liquidado".', 'P'],
]
for i, row_data in enumerate(prueba_rows, 1):
    bg = "F0F4F8" if i % 2 == 0 else None
    data_row(table_prueba, i, row_data, bg)

add_note(doc, 'E = Prueba ejecutada; P = Prueba planeada para Incremento 5. Las pruebas PT-06, PT-08 y PT-10 se ejecutarán con usuarios reales del salón en la validación final.')

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 12. MOCKUPS
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '12. Mockups de la Aplicación y Recorrido de Usuario', 1)
add_body(doc,
    'Los mockups a continuación describen las pantallas principales del sistema en su estado '
    'actual de implementación. Los esquemas se presentan en representación textual dado el '
    'formato del documento; las capturas de pantalla de la interfaz real se encuentran en '
    'la carpeta /screenshots del repositorio GitHub del proyecto.'
)

mockups = [
    ('Figura 5', 'Pantalla de Inicio de Sesión (LoginSystemAremi)', """\
 ╔══════════════════════════════════════════════╗
 ║          🌸 SALÓN DE BELLEZA AREMI            ║
 ║                                              ║
 ║   Usuario: [________________________]        ║
 ║                                              ║
 ║   Contraseña: [••••••••••••••••••••]        ║
 ║                                              ║
 ║         [ Iniciar Sesión ]                   ║
 ║                                              ║
 ║   ⚙️ Configurar conexión BD                  ║
 ╚══════════════════════════════════════════════╝""",
     'Pantalla de inicio de sesión. Paleta de colores institucional (morado/rosado). Muestra campo de usuario, contraseña enmascarada y botón de acceso. Acceso al configurador de BD desde la pantalla de login.'),
    ('Figura 6', 'Menú Principal (MainApplication) — Vista Administradora', """\
 ╔══════════════════════════════════════════════════════╗
 ║   🌸 SALÓN AREMI — Bienvenida, Administradora       ║
 ╠══════════════════════════════════════════════════════╣
 ║                                                      ║
 ║  [👤 Clientes]   [👩 Empleadas]   [✂️ Servicios]   ║
 ║                                                      ║
 ║  [📅 Agenda]  [✅ Serv. Realizados]  [💰 Liquidar]  ║
 ║                                                      ║
 ║  [🔒 Cerrar Sesión]                                  ║
 ╚══════════════════════════════════════════════════════╝""",
     'Menú principal para el rol Administradora. Muestra todas las funciones disponibles organizadas en botones con iconos. Las empleadas ven solo los botones Agenda y Servicios Realizados.'),
    ('Figura 7', 'Módulo de Agendamiento de Citas (AgendarCitaGUI)', """\
 ╔══════════════════════════════════════════════════════╗
 ║   📅 AGENDAR CITA                                    ║
 ╠══════════════════════════════════════════════════════╣
 ║  Clienta:  [▼ Seleccionar clienta ____________]     ║
 ║  Empleada: [▼ Seleccionar empleada ___________]     ║
 ║  Servicio: [▼ Manicure - $25.000 _____________]     ║
 ║  Fecha:    [📅 2026-11-15]                           ║
 ║  Hora:     [10:00]  Duración: [60 min]              ║
 ║                                                      ║
 ║  ┌──────────────────────────────────────────────┐   ║
 ║  │  AGENDA SEMANAL — Ashley Rodríguez           │   ║
 ║  │  LUN │ MAR │ MIÉ │ JUE │ VIE                 │   ║
 ║  │ 9:00 │     │10:00│     │11:00                │   ║
 ║  └──────────────────────────────────────────────┘   ║
 ║                                                      ║
 ║  ⚠️ La empleada ya tiene cita de 10:00 a 11:00      ║
 ║                                                      ║
 ║      [Confirmar Cita]    [Cancelar]                  ║
 ╚══════════════════════════════════════════════════════╝""",
     'Módulo de agendamiento con vista de agenda semanal de la empleada seleccionada. El sistema muestra advertencia en tiempo real cuando hay conflicto de horario. El botón "Confirmar" se bloquea si hay cruce.'),
    ('Figura 8', 'Módulo de Liquidación de Comisiones (LiquidacionComisionesGUI)', """\
 ╔══════════════════════════════════════════════════════╗
 ║   💰 LIQUIDACIÓN DE COMISIONES                       ║
 ╠══════════════════════════════════════════════════════╣
 ║  Empleada:   [▼ Monika Pérez ________________]      ║
 ║  Desde:      [2026-11-01]   Hasta: [2026-11-15]     ║
 ║                 [🔍 Calcular]                        ║
 ╠══════════════════════════════════════════════════════╣
 ║  ┌──────────────┐ ┌──────────────┐ ┌─────────────┐  ║
 ║  │ TOTAL VENTAS │ │ COMISIÓN 30% │ │ SERVICIOS   │  ║
 ║  │  $120.000    │ │   $36.000    │ │     4       │  ║
 ║  └──────────────┘ └──────────────┘ └─────────────┘  ║
 ╠══════════════════════════════════════════════════════╣
 ║  Fecha   │ Servicio    │ Valor   │ Comisión           ║
 ║  Nov 03  │ Manicure    │ $25.000 │ $7.500             ║
 ║  Nov 05  │ Pedicure    │ $30.000 │ $9.000             ║
 ║  Nov 08  │ Tinte       │ $35.000 │ $10.500            ║
 ║  Nov 11  │ Corte       │ $30.000 │ $9.000             ║
 ╠══════════════════════════════════════════════════════╣
 ║  Notas: [__________________________________________]  ║
 ║      [✅ Procesar Liquidación y Generar PDF]          ║
 ╚══════════════════════════════════════════════════════╝""",
     'Módulo de liquidación. Muestra tarjetas de resumen (total ventas, comisión, número de servicios) y tabla detallada. Al procesar, se ejecuta la transacción ACID y se genera el comprobante PDF.'),
]

for fig_num, fig_title, ascii_art, caption_text in mockups:
    add_heading(doc, fig_title.split('. ')[1] if '. ' in fig_title else fig_title, 2)
    add_monospace(doc, ascii_art)
    add_figure_caption(doc, fig_num.replace('Figura ', ''), f'{fig_title.split(". ")[1] if ". " in fig_title else fig_title}. {caption_text} Elaboración propia.')
    doc.add_paragraph()

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 13. CONCLUSIONES
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '13. Conclusiones y Trabajo Futuro', 1)
conclusiones = [
    'La adopción de la metodología de desarrollo incremental ha permitido al equipo estructurar de manera '
    'organizada el ciclo de vida del software en cinco incrementos funcionales verificables. '
    'Este enfoque facilita la validación progresiva con la usuaria real del salón y reduce el riesgo '
    'de entregar un producto que no corresponda a las necesidades identificadas (Sommerville, 2019).',

    'La refactorización de la arquitectura hacia MVC+DAO eliminó los cuatro puntos de acceso directo '
    'a la base de datos desde la capa Vista, resolviendo la vulnerabilidad de la contraseña hardcodeada '
    'y reduciendo el acoplamiento a cero entre la interfaz gráfica y la lógica de persistencia. '
    'Este cambio es consistente con los principios de código limpio descritos por Martin (2017).',

    'La especificación de los diez requerimientos funcionales con criterios de aceptación individuales '
    'en formato Gherkin estableció una base verificable para las pruebas de aceptación. '
    'La distinción entre el porcentaje de comisión vigente y el porcentaje histórico congelado en '
    'ServicioRealizado resuelve el problema de inmutabilidad de liquidaciones históricas, '
    'garantizando la integridad financiera del sistema.',

    'La distribución del trabajo en el repositorio Git, con commits atribuidos a cada integrante '
    'según su módulo (Erick: Agenda y Citas; Jeram: Finanzas y PDF; Yeneicy: Persistencia y Seguridad), '
    'refleja la contribución real de cada miembro del equipo y facilita la evaluación individual.',

    'Como trabajo futuro para los incrementos restantes, se proyecta: completar las pruebas de '
    'aceptación con usuarios reales del salón, desarrollar el módulo de estadísticas operativas '
    '(número de servicios por período, empleada más productiva), y evaluar la portabilidad del '
    'sistema a una arquitectura web si el volumen de operaciones lo justifica.',
]
for i, c in enumerate(conclusiones, 1):
    p = doc.add_paragraph()
    r = p.add_run(f'{i}. ')
    r.bold = True; r.font.size = Pt(12); r.font.name = 'Times New Roman'
    r2 = p.add_run(c)
    r2.font.size = Pt(12); r2.font.name = 'Times New Roman'
    p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    p.paragraph_format.space_after = Pt(6)

doc.add_page_break()

# ══════════════════════════════════════════════════════════════════
# 14. REFERENCIAS (APA 7, orden alfabético, sangría francesa)
# ══════════════════════════════════════════════════════════════════
add_heading(doc, '14. Referencias Bibliográficas', 1)
add_body(doc,
    'Las siguientes catorce referencias académicas y profesionales han sido citadas a lo largo '
    'del documento y corresponden a fuentes verificables. Se presentan en orden alfabético por '
    'apellido del autor, con sangría francesa, en conformidad con el formato APA 7 (American '
    'Psychological Association, 2020).'
)

referencias = [
    'Bloch, J. (2018). \u202fEffective Java\u202f (3.ª ed.). Addison-Wesley Professional.',
    'Congreso de la República de Colombia. (2000, 10 de julio). \u202fLey 603 de 2000: Por la cual se modifica el artículo 47 de la Ley 222 de 1995\u202f. Diario Oficial n.° 44.084. https://www.sic.gov.co/sites/default/files/normatividad/Ley_603_2000.pdf',
    'Congreso de la República de Colombia. (2012, 17 de octubre). \u202fLey Estatutaria 1581 de 2012: Por la cual se dictan disposiciones generales para la protección de datos personales\u202f. Diario Oficial n.° 48.587. https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=49981',
    'Elmasri, R., & Navathe, S. B. (2017). \u202fFundamentos de sistemas de bases de datos\u202f (7.ª ed.). Pearson Educación.',
    'Fowler, M. (2014). \u202fUML Distilled: A Brief Guide to the Standard Object Modeling Language\u202f (3.ª ed.). Addison-Wesley Professional.',
    'Fowler, M. (2018). \u202fRefactoring: Improving the Design of Existing Code\u202f (2.ª ed.). Addison-Wesley Professional.',
    'Freeman, E., & Robson, E. (2020). \u202fHead First Design Patterns: Building Extensible and Maintainable Object-Oriented Software\u202f (2.ª ed.). O\'Reilly Media.',
    'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). \u202fDesign Patterns: Elements of Reusable Object-Oriented Software\u202f. Addison-Wesley Professional.',
    'Martin, R. C. (2017). \u202fClean Architecture: A Craftsman\'s Guide to Software Structure and Design\u202f. Prentice Hall.',
    'Oracle Corporation. (2023a). \u202fJava Database Connectivity (JDBC) Guide: Prepared Statements and Connection Management\u202f. https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/PreparedStatement.html',
    'Oracle Corporation. (2023b). \u202fTrail: Creating a GUI With JFC/Swing\u202f. https://docs.oracle.com/javase/tutorial/uiswing/index.html',
    'Pressman, R. S., & Maxim, B. R. (2020). \u202fIngeniería del software: Un enfoque práctico\u202f (9.ª ed.). McGraw-Hill Education.',
    'Sommerville, I. (2019). \u202fIngeniería de Software\u202f (10.ª ed.). Pearson Educación.',
    'Stallings, W. (2018). \u202fCriptografía y seguridad de redes: Principios y práctica\u202f (7.ª ed.). Pearson Educación.',
]

for ref in referencias:
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    # Sangría francesa APA 7: primera línea al margen, subsiguientes con sangría
    p.paragraph_format.left_indent = Cm(1.27)
    p.paragraph_format.first_line_indent = Cm(-1.27)
    p.paragraph_format.space_after = Pt(6)
    run = p.add_run(ref)
    run.font.size = Pt(12)
    run.font.name = 'Times New Roman'

# ══════════════════════════════════════════════════════════════════
# GUARDAR
# ══════════════════════════════════════════════════════════════════
output_path = r'C:\Users\eenriquez\Downloads\Proyecto_Estetica_Aremi-main\2_Avance_PI_Ingenieria_Software_Aremi_v2.docx'
doc.save(output_path)
print(f'\n✅ Documento Word COMPLETO guardado en:\n   {output_path}')
print('\nSecciones incluidas:')
print('  ✓ Portada APA 7 correcta')
print('  ✓ Resumen con resultados alcanzados vs esperados + palabras clave')
print('  ✓ Introducción 4 párrafos con contexto, necesidad, propósito y organización')
print('  ✓ Marco del problema, justificación, objetivos, marco referencial')
print('  ✓ Cronograma 5 incrementos con responsables y entregables (Tabla 1)')
print('  ✓ Matriz de responsables del equipo (Tabla 2)')
print('  ✓ 10 RF con Gherkin, validaciones y reglas de negocio')
print('  ✓ 7 RNF con métricas de verificación')
print('  ✓ Matriz RBAC (Tabla 3)')
print('  ✓ 8 casos de uso especificados (CU-01 a CU-08)')
print('  ✓ Diagrama de casos de uso ASCII (Figura 1)')
print('  ✓ Diagrama de actividades: agendamiento (Figura 2)')
print('  ✓ Diagrama de actividades: liquidación (Figura 3)')
print('  ✓ Diagrama de clases UML dominio real (Figura 4)')
print('  ✓ MER como tabla detallada (Tabla 4)')
print('  ✓ Consulta SQL de liquidación')
print('  ✓ Plan de pruebas con estado E/P (Tabla 5)')
print('  ✓ 4 Mockups ASCII de pantallas principales (Figuras 5-8)')
print('  ✓ Conclusiones (5 puntos) con trabajo futuro')
print('  ✓ 14 referencias APA 7, alfabéticas, sangría francesa')
