package h03.utils.transformer;

import org.objectweb.asm.*;
import org.sourcegrade.jagr.api.testing.ClassTransformer;

public class AccessTransformer implements ClassTransformer {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void transform(ClassReader reader, ClassWriter writer) {
        reader.accept(new ClassTransformer(writer), ClassReader.EXPAND_FRAMES);
    }

    @Override
    public int getWriterFlags() {
        return ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
    }

    private static class ClassTransformer extends ClassVisitor {

        public ClassTransformer(ClassWriter classWriter) {
            super(Opcodes.ASM9, classWriter);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            return super.visitField(access & (-1 - (Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC,
                name,
                descriptor,
                signature,
                value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return super.visitMethod(access & (-1 - (Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC,
                name,
                descriptor,
                signature,
                exceptions);
        }
    }
}
