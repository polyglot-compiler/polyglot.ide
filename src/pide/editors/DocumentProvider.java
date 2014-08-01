package pide.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * Factory for creating Documents. Hooked in by {@link Editor#Editor()}.
 */
public class DocumentProvider extends FileDocumentProvider {

  @Override
  protected IDocument createEmptyDocument() {
    return new Document();
  }

  @Override
  protected IDocument createDocument(Object element) throws CoreException {
    IDocument document = super.createDocument(element);
    if (document != null) {
      // Hook in the document partitioner.
      IDocumentPartitioner partitioner =
          new FastPartitioner(PartitionScanner.INSTANCE,
              PartitionScanner.PARTITION_TYPES);
      partitioner.connect(document);
      document.setDocumentPartitioner(partitioner);
    }
    return document;
  }

}
