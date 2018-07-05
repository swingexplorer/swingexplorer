SwingExplorer Code Style
========================

# General

The product name is styled "Swing Explorer" with a space, not "SwingExplorer".

# Source code formatting

Indent with 4 spaces, not tabs.

### Spacing

Use a single blank line at the beginning of class definitions. No blank line at the end of class definitions.

Example:

```java
class Foo {

    private int x;
}
```

Single blank lines between methods.

Group field definitions tightly, with blank lines to separate logical groups.

# Naming conventions

SwingExplorer mostly uses standard Java naming conventions.

### Underscores for args that conflict with fields

If you have a method with an argument that would have the same name as a field (as with property setters), prefix the argument name with an underscore. This is used instead of disambiguating identically-named arguments and fields with a `this.<field>` qualifier.

Example:

```java
class Person {
    private String name;

    public void setName(String _name) {
        name = _name;
    }
}
```

### Prefixes for component types

SwingExplorer mostly uses class names with abbreviated prefixes like "`ActDisplayParent`" instead of suffixes like "`DisplayParentAction`".
The main idea behind the prefix notation is to facilitate class searching in IDE using prefix and Ctrl+Space.

| Prefix | Meaning |
| ------ | ------- |
| Act    | Action  |
| Dlg    | Dialog  |
| Frm    | Form    |
| Mdl    | Model   |
| Pnl    | Panel   |
