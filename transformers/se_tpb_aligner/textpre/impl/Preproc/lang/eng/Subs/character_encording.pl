#!/usr/bin/perl -w

sub character_encoding {

	my $string = shift;
	
	@string = split//,$string;
	
	foreach my $s ( @string ) {
		
		if ( exists ( $char_norm{ $s } )) {
			$s = $char_norm{ $s };
		}
	}
	
	$string = join"",@string;
	return $string;
}
1;
