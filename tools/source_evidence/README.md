# Source evidence for verified cable records (V0.5.5)

One text file per record id. Each file holds the lines copied **verbatim** from the
manufacturer's own datasheet on the fetch date shown. `tools/VerifyLibraryData.java`
checks that every number written into a `VerifiedCableData` record also appears in
its evidence file, so a typo (5.98 instead of 5.89) fails the build.

How the quotes were obtained: the datasheet URL was read with an automated
page-to-text extractor, twice, with two differently worded prompts; both passes agreed
on every value used. This is **not** a substitute for opening the PDF: before selling
or relying on a record, open its `pdfUrl` from the app and compare.

A value the datasheet does not state is `null` / "not published" in the app and is
never filled from memory or from another cable.
