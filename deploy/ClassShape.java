import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Prints the shape of compiled classes for deploy/patch-classes.py: fields, methods and
 * instructions with string constants replaced by "S" and jump targets by label order, so two
 * builds of the same source that differ only in string literals print the same text.
 *
 * Usage: java -cp asm.jar:. ClassShape <classes dir> <class name>...
 * Output: "=== <class name>" followed by its shape, or "=== <class name> MISSING".
 */
public class ClassShape {

	public static void main(String[] args) throws Exception {
		Path root = Paths.get(args[0]);
		StringBuilder out = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			Path p = root.resolve(args[i].replace('.', '/') + ".class");
			out.append("=== ").append(args[i]);
			if (!Files.exists(p)) {
				out.append(" MISSING\n");
				continue;
			}
			out.append('\n');
			try (InputStream in = Files.newInputStream(p)) {
				new ClassReader(in).accept(new Shape(out), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
			}
		}
		System.out.print(out);
	}

	static String constant(Object v) {
		if (v instanceof String) {
			return "S";
		}
		if (v instanceof Type) {
			return "T" + ((Type) v).getDescriptor();
		}
		if (v instanceof Handle) {
			Handle h = (Handle) v;
			return "H" + h.getTag() + h.getOwner() + "." + h.getName() + h.getDesc();
		}
		return String.valueOf(v);
	}

	static final class Shape extends ClassVisitor {
		private final StringBuilder out;

		Shape(StringBuilder out) {
			super(Opcodes.ASM9);
			this.out = out;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			out.append("class ").append(access).append(' ').append(name).append(" extends ").append(superName);
			if (interfaces != null) {
				for (String i : interfaces) {
					out.append(' ').append(i);
				}
			}
			out.append('\n');
		}

		@Override
		public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
			out.append("field ").append(access).append(' ').append(name).append(' ').append(descriptor);
			if (value != null) {
				out.append(" = ").append(constant(value));
			}
			out.append('\n');
			return null;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
				String[] exceptions) {
			out.append("method ").append(access).append(' ').append(name).append(descriptor).append('\n');
			return new Code(out);
		}
	}

	static final class Code extends MethodVisitor {
		private final StringBuilder out;
		private final java.util.Map<Label, Integer> labels = new java.util.IdentityHashMap<>();

		Code(StringBuilder out) {
			super(Opcodes.ASM9);
			this.out = out;
		}

		private int id(Label l) {
			return labels.computeIfAbsent(l, k -> labels.size());
		}

		private void op(int opcode, String rest) {
			out.append("  ").append(opcode).append(rest.isEmpty() ? "" : " " + rest).append('\n');
		}

		@Override
		public void visitInsn(int opcode) {
			op(opcode, "");
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			op(opcode, String.valueOf(operand));
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			op(opcode, String.valueOf(var));
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			op(opcode, type);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
			op(opcode, owner + "." + name + " " + descriptor);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
			op(opcode, owner + "." + name + descriptor);
		}

		@Override
		public void visitInvokeDynamicInsn(String name, String descriptor, Handle bsm, Object... args) {
			StringBuilder b = new StringBuilder(name).append(descriptor).append(' ').append(constant(bsm));
			for (Object a : args) {
				b.append(' ').append(constant(a));
			}
			op(Opcodes.INVOKEDYNAMIC, b.toString());
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			op(opcode, "L" + id(label));
		}

		@Override
		public void visitLabel(Label label) {
			out.append(" L").append(id(label)).append('\n');
		}

		@Override
		public void visitLdcInsn(Object value) {
			op(Opcodes.LDC, constant(value));
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			op(Opcodes.IINC, var + " " + increment);
		}

		@Override
		public void visitTableSwitchInsn(int min, int max, Label dflt, Label... ls) {
			StringBuilder b = new StringBuilder(min + ".." + max + " default L" + id(dflt));
			for (Label l : ls) {
				b.append(" L").append(id(l));
			}
			op(Opcodes.TABLESWITCH, b.toString());
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] ls) {
			StringBuilder b = new StringBuilder("default L" + id(dflt));
			for (int i = 0; i < keys.length; i++) {
				b.append(' ').append(keys[i]).append(":L").append(id(ls[i]));
			}
			op(Opcodes.LOOKUPSWITCH, b.toString());
		}

		@Override
		public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
			op(Opcodes.MULTIANEWARRAY, descriptor + " " + numDimensions);
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			out.append("  try L").append(id(start)).append(" L").append(id(end)).append(" L").append(id(handler))
					.append(' ').append(type).append('\n');
		}
	}
}
