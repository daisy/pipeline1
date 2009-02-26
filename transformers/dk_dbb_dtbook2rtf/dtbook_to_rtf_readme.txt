Usage:

Call xslt dtbook_to_rtf.xsl to generate a plain RTF document (with no TOC)

Call xslt dtbook_to_rtf_with_toc.xsl to generate a RTF document with an auto generated TOC (levels 1-3).
To update pagenumbers in TOC in MS WORD Press Ctrl+A followed by F9

Known issues:

  * @dir attribute (right to left text) is not supported
  * col/colgroup in tables is not supported
  * @colspan and @rowspan are not supported in tables

TO-DO:

  * Add support for @align and @valign in tables
