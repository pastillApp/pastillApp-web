@(seq: Seq[Manager])
@main("Index") {
	<h1>pastillApp</h1>
	<h2>Contacts</h2>
	<ul class="unstyled">
		@for(manager <- seq) {
			<li class="row-fluid">
				<span class="btn span10">@manager.name</span><a class="btn btn-danger span1 offset1">X</a>
			</li>
		}
	</ul>
	<div class="row-fluid">
		<span class="btn span12 btn-success">Add manager</span>
	</div>
}